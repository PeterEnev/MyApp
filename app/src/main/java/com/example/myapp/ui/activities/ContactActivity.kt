package com.example.myapp.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.R
import com.example.myapp.databinding.ActivityContactBinding
import com.example.myapp.helperClasses.Utils
import com.example.myapp.helperClasses.UtilsDefines
import com.example.myapp.models.*
import com.example.myapp.presenters.ContactPresenter
import com.example.myapp.presenters.ContactView
import com.example.myapp.ui.adapters.*
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.country_search_dialog.*

private const val DEFAULT_VALUE_NEW_CONTACT             = -1L
private const val DATA_UPDATE                           = 1
private const val DATA_DELETE                           = 2
private const val DATA_CREATE                           = 3

private const val EMPTY_STRING                          = ""
private const val CONTACT_EXISTING_BOOLEAN_EXTRA        = "existing"
private const val RESULT                                = "result"

class ContactActivity : AppCompatActivity(), ContactView, EmailAdapterListener, PhoneAdapterListener {

    private lateinit var bindingContact             : ActivityContactBinding
    private lateinit var contactPresenter           : ContactPresenter
    private lateinit var phoneAdapter               : PhoneAdapter
    private lateinit var emailAdapter               : EmailAdapter
    private lateinit var editingContact             : Contact
    private lateinit var contact                    : Contact

    private          var deletedPhones              = mutableListOf<ContactPhone>()
    private          var deletedEmails              = mutableListOf<ContactEmail>()
    private          var contactEmails              = mutableListOf<ContactEmail>()
    private          var contactEmailList           = mutableListOf<ContactEmail>()
    private          var contactPhones              = mutableListOf<ContactPhone>()
    private          var contactPhoneList           = mutableListOf<ContactPhone>()

    private          var contactStatusExisting      = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingContact          = DataBindingUtil.setContentView(this, R.layout.activity_contact)
        contactPresenter        = ContactPresenter(this)
        contactStatusExisting   = intent.getBooleanExtra(CONTACT_EXISTING_BOOLEAN_EXTRA, false)


        if (contactStatusExisting){
            editingContact = intent.getSerializableExtra(UtilsDefines.CONTACT_SERIALIZABLE_EXTRA) as Contact

            contactPhoneList.addAll(editingContact.contactPhoneNumber!!)
            contactEmailList.addAll(editingContact.contactEMail!!)


            firstNameInput.setText(editingContact.contactFirstName)
            lastNameInput.setText(editingContact.contactLastName)
            countryInput.setText(editingContact.contactCountryName)
            saveEditContactBtn.setText(R.string.BTN_EDIT)
        } else {
            contactEmailList.add(
                ContactEmail(null,
                             if (contactStatusExisting) editingContact.contactID else null,
                             EMPTY_STRING,
                             EMPTY_STRING,
                             DATA_CREATE))

            contactPhoneList.add(
                ContactPhone(null,
                             if (contactStatusExisting) editingContact.contactID else null,
                             EMPTY_STRING,
                             EMPTY_STRING,
                             DATA_CREATE))
        }

        phoneAdapter                        = PhoneAdapter(contactPhoneList, this, this)
        emailAdapter                        = EmailAdapter(contactEmailList, this, this)
        emailRecyclerView.adapter           = emailAdapter
        emailRecyclerView.layoutManager     = LinearLayoutManager(this)

        phoneRecyclerView.adapter           = phoneAdapter
        phoneRecyclerView.layoutManager     = LinearLayoutManager(this)

        firstNameInput.setOnFocusChangeListener { _, hasFocus ->
            if (contactStatusExisting && editingContact.contactFirstName != firstNameInput.text.toString()){
                editingContact.contactFirstName     = firstNameInput.text.toString()
                editingContact.contactEdit          = DATA_UPDATE
            }
            errorHandler(
                firstNameTxt,
                !hasFocus && firstNameInput.text!!.count() < 3
            )
        }

        lastNameInput.setOnFocusChangeListener{ _, hasFocus ->
            if (contactStatusExisting && editingContact.contactLastName != lastNameInput.text.toString()){
                editingContact.contactLastName      = lastNameInput.text.toString()
                editingContact.contactEdit          = DATA_UPDATE
            }
            errorHandler(
                lastNameTxt,
                !hasFocus && lastNameInput.text!!.count() < 3
            )
        }

        countryInput.setOnClickListener {
            if (contactStatusExisting) editingContact.contactEdit = DATA_UPDATE
            contactPresenter.getCountryNames()
        }
    }

    fun saveContact(view: View){
        if (contactStatusExisting){
            with(editingContact){contact = Contact(contactID,
                                                    contactFirstName,
                                                    contactLastName,
                                                    contactCountryName,
                                                    getPhoneData(),
                                                    getEmailData(),
                                                    contactLocalStorageStats,
                                                    contactPhoto,
                                                    contactEdit)}
            contactPresenter.editContact(contact)
        } else {
            contactPresenter.saveContact(getContactData())
        }
    }

    private fun getContactData() :Contact{
        return Contact(contactID                   = DEFAULT_VALUE_NEW_CONTACT,
                       contactFirstName            = firstNameInput.text.toString(),
                       contactLastName             = lastNameInput.text.toString(),
                       contactCountryName          = countryInput.text.toString(),
                       contactPhoneNumber          = getPhoneData(),
                       contactEMail                = getEmailData(),
                       contactLocalStorageStats    = true,
                       contactPhoto                = null)
    }

    private fun getEmailData(): List<ContactEmail>{
        contactEmails.addAll(contactEmailList)
        contactEmails.addAll(deletedEmails)
        return contactEmails
    }

    private fun getPhoneData(): List<ContactPhone>{
        contactPhones.addAll(contactPhoneList)
        contactPhones.addAll(deletedPhones)
        return contactPhones
    }

    private fun errorHandler(textLayout: TextInputLayout, condition: Boolean) {
        if(condition){
            textLayout.setErrorEnabled(true)
            textLayout.error = getString(
                when (textLayout) {
                    firstNameTxt    -> R.string.MSG_ENTER_VALID_FIRST_NAME
                    lastNameTxt     -> R.string.MSG_ENTER_VALID_LAST_NAME
                    else            -> R.string.MSG_ENTER_COUNTRY
                }
            )
        } else {
            textLayout.setErrorEnabled(false)
        }
    }

    override fun notifyDataChangedPhoneRow(position: Int, phone: String, type: String) {
        contactPhoneList[position].phone                = phone
        contactPhoneList[position].contactPhoneType     = type
        if (contactPhoneList[position].phoneEdit != DATA_CREATE)
            contactPhoneList[position].phoneEdit = DATA_UPDATE
    }

    override fun notifyDataChangedEmailRow(position: Int, email: String, type: String){
        contactEmailList[position].email                = email
        contactEmailList[position].contactEmailType     = type
        if (contactEmailList[position].emailEdit != DATA_CREATE)
            contactEmailList[position].emailEdit = DATA_UPDATE
    }

    override fun addNewPhoneRow(id: Int) {
        contactPhoneList.add(ContactPhone(null,
                                            if (contactStatusExisting) editingContact.contactID else null,
                                            EMPTY_STRING,
                                            EMPTY_STRING,
                                            DATA_CREATE))
    }

    override fun addNewEmailRow(id: Int) {
        contactEmailList.add(ContactEmail(null,
                                            if (contactStatusExisting) editingContact.contactID else null,
                                            EMPTY_STRING,
                                            EMPTY_STRING,
                                            DATA_CREATE))
    }

    override fun deletePhoneRow(position: Int) {
        if (contactPhoneList[position].contactPhoneId != null){
            contactPhoneList[position].phoneEdit = DATA_DELETE
            deletedPhones.add(contactPhoneList[position])
            contactPhoneList.removeAt(position)
        } else {
            contactPhoneList.removeAt(position)
        }
    }

    override fun deleteEmailRow(position: Int) {
        if (contactEmailList[position].contactEmailId != null){
            contactEmailList[position].emailEdit = DATA_DELETE
            deletedEmails.add(contactEmailList[position])
            contactEmailList.removeAt(position)
        } else {
            contactEmailList.removeAt(position)
        }
    }

    override fun openChoiceCountryDialog(listCountry: ArrayList<Country>) {
        val dialog              = Dialog(this)
        var listCountryRenew    = listCountry
        val adapter             = CountryListAdapter(this, listCountryRenew)

        with(dialog){
            setCancelable(false)
            setTitle(getString(R.string.COUNTRY_DIALOG_TITLE))
            setContentView(R.layout.country_search_dialog)
            countryListView.adapter = adapter
            setCanceledOnTouchOutside(true)

            choiceCountryCancelBtn.setOnClickListener{ dialog.dismiss() }

            searchCountryTxt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    for (i in 0 until listCountry.size) {
                        if ((listCountry[i].countryName).contains(s.toString().toUpperCase())) {
                            listCountryRenew.add(listCountry[i])
                        }
                    }
                    adapter.updateList(listCountryRenew)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    listCountryRenew = ArrayList()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
        dialog.countryListView.setOnItemClickListener { _, _, position, _ ->
            if (contactStatusExisting){
                editingContact.contactCountryName = listCountryRenew[position].countryName
            }
            countryInput.setText(listCountryRenew[position].countryName)
            errorHandler(countryInputLayout, false)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun toastMsg(result: Int) {
        Toast.makeText(this, Utils().getToastMsg(result), Toast.LENGTH_SHORT).show()
    }

    override fun navigateToMainActivity(result: Int) {
        toastMsg(result)
        finish()
    }
}
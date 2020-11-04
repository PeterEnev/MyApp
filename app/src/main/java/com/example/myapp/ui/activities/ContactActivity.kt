package com.example.myapp.ui.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ActivityInfoCompat
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.models.Country
import com.example.myapp.presenters.ContactPresenter
import com.example.myapp.presenters.ContactView
import com.example.myapp.ui.adapters.ContactListAdapter
import com.example.myapp.ui.adapters.CountryListAdapter
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.contact_dialog.*
import kotlinx.android.synthetic.main.country_search_dialog.*
import kotlin.contracts.Returns

class ContactActivity : AppCompatActivity(), ContactView {

    private lateinit var contactPresenter: ContactPresenter

    companion object {
        val CONTACT                     :String             = "contact"
        val DEFAULT_VALUE_NEW_CONTACT   :Long               = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        contactPresenter = ContactPresenter(this)

        val contactStatus  = intent.getStringExtra(ContactListAdapter.CONTACT_STATUS)
        var contactId       = DEFAULT_VALUE_NEW_CONTACT

        if (contactStatus == ContactListAdapter.CONTACT_STATUS_EXISTING) {
            val editContact = intent.getSerializableExtra(CONTACT) as? Contact
            contactId = editContact!!.contactID.toLong()
            firstNameInput  .setText(editContact.contactFirstName)
            lastNameInput   .setText(editContact.contactLastName)
            countryInput    .setText(editContact.contactCountryName)
            prefixInput     .setText(editContact.contactCountryPrefix)
            phoneInput      .setText(editContact.contactPhoneNumber)
            eMailInput      .setText(editContact.contactEMail)
            genderInput     .setText(editContact.contactGender)
        }

        selectCountryBtn.setOnClickListener { contactPresenter.getCountryNames() }

        saveEditContactBtn.setOnClickListener {
            var contact: Contact
            if (contactStatus == ContactListAdapter.CONTACT_STATUS_EXISTING) {
                contact = Contact(
                    contactFirstName            = firstNameInput.text.toString(),
                    contactLastName             = lastNameInput.text.toString(),
                    contactCountryName          = countryInput.text.toString(),
                    contactCountryPrefix        = prefixInput.text.toString(),
                    contactPhoneNumber          = phoneInput.text.toString(),
                    contactEMail                = eMailInput.text.toString(),
                    contactGender               = genderInput.text.toString(),
                    contactLocalStorageStats    = contactStatus.toBoolean(),
                    contactID                   = contactId
                )
            } else {
                contact = Contact(
                    contactFirstName            = firstNameInput.text.toString(),
                    contactLastName             = lastNameInput.text.toString(),
                    contactCountryName          = countryInput.text.toString(),
                    contactCountryPrefix        = prefixInput.text.toString(),
                    contactPhoneNumber          = phoneInput.text.toString(),
                    contactEMail                = eMailInput.text.toString(),
                    contactGender               = genderInput.text.toString(),
                    contactLocalStorageStats    = false,
                    contactID                   = contactId
                )
            }
            contactPresenter.saveContactDialog(contact)
        }
    }

    override fun openChoiceCountryDialog(listCountry: ArrayList<Country>) {
        val dialog: Dialog = Dialog(this)
        var listCountryRenew = listCountry
        var adapter = CountryListAdapter(this, listCountryRenew)
        dialog.setCancelable(false)
        dialog.setTitle(getString(R.string.COUNTRY_DIALOG_TITLE))
        dialog.setContentView(R.layout.country_search_dialog)
        dialog.countryListView.adapter = adapter
        dialog.setCanceledOnTouchOutside(true)

        dialog.choiceCountryCancelBtn.setOnClickListener{ dialog.dismiss() }

        dialog.searchCountryTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                for (i in 0 until listCountry.size) {
                    if ((listCountry[i].countryName).contains(s.toString().toUpperCase())) {
                        listCountryRenew.add(listCountry[i])
                    }
                }
                adapter = CountryListAdapter(
                    context         = this@ContactActivity,
                    listCountry     = listCountryRenew
                )
                dialog.countryListView.adapter = adapter
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listCountryRenew = ArrayList()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        dialog.countryListView.setOnItemClickListener { parent, view, position, id ->
            countryInput.setText(listCountryRenew[position].countryName)
            prefixInput.setText(listCountryRenew[position].countryPrefih)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun saveContactDialog(contact: Contact) {
        val dialog: Dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setTitle(getString(R.string.CONTACT_DIALOG_TITLE))
        dialog.setContentView(R.layout.contact_dialog)
        dialog.setCanceledOnTouchOutside(true)

        dialog.dialogContactNameTxt.setText(
            contact.contactFirstName + " " + contact.contactLastName
        )
            .toString()
        dialog.dialogContactPhoneTxt.setText(
            contact.contactCountryPrefix + contact.contactPhoneNumber
        )
            .toString()
        dialog.dialogContactMailTxt.setText(
            contact.contactEMail
        )
            .toString()
        dialog.dialogContactCountryTxt.setText(
            contact.contactCountryName
        )
            .toString()
        dialog.dialogContactGenderTxt.setText(
            contact.contactGender
        )
            .toString()

        if (contact.contactID < 0){
            dialog.dialogEditBtn.setText(getString(R.string.BTN_SAVE))
        }else{
            dialog.dialogEditBtn.setText(getString(R.string.BTN_UPDATE))
        }

        dialog.dialogCancelBtn.setOnClickListener { dialog.dismiss() }

        dialog.dialogEditBtn.setOnClickListener {
            if (contact.contactID < 0){
                contactPresenter.saveContact(contact)
            }else{
                contactPresenter.editContact(contact)
            }
        }
        dialog.show()
    }

    override fun toastMsg(status: Int) {
        var message: String
        when(status){
            0 -> message = getString(R.string.MSG_SAVE_SUCCESSFUL)
            1 -> message = getString(R.string.MSG_ENTER_VALID_FIRST_NAME)
            2 -> message = getString(R.string.MSG_ENTER_VALID_LAST_NAME)
            3 -> message = getString(R.string.MSG_ENTER_VALID_EMAIL_ADDRESS)
            4 -> message = getString(R.string.MSG_ENTER_VALID_PHONE_NUMBER)
            5 -> message = getString(R.string.MSG_ENTER_VALID_GENDER)
            else -> {
                message = getString(R.string.MSG_OOPS)
            }
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToMainActivity(status: Int) {
        val message = getString(R.string.MSG_SAVE_SUCCESSFUL)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("result",0)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}
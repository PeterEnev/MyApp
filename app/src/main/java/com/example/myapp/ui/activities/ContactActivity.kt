package com.example.myapp.ui.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapp.R
import com.example.myapp.databinding.ActivityContactBinding
import com.example.myapp.models.*
import com.example.myapp.presenters.ContactPresenter
import com.example.myapp.presenters.ContactView
import com.example.myapp.ui.activities.MainActivity.Companion.CONTACT_EXISTING_BOOLEAN_EXTRA
import com.example.myapp.ui.activities.MainActivity.Companion.CONTACT_SERIALIZABLE_EXTRA
import com.example.myapp.ui.activities.MainActivity.Companion.CONTACT_STATUS_EXISTING
import com.example.myapp.ui.activities.MainActivity.Companion.CONTACT_STATUS_NEW
import com.example.myapp.ui.adapters.CountryListAdapter
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.activity_contact.eMailInput
import kotlinx.android.synthetic.main.activity_contact.emailTxt
import kotlinx.android.synthetic.main.activity_contact.typeEmail
import kotlinx.android.synthetic.main.contact_dialog.*
import kotlinx.android.synthetic.main.country_search_dialog.*
import kotlinx.android.synthetic.main.list_item_email.*
import kotlinx.android.synthetic.main.list_item_email.view.*
import kotlinx.android.synthetic.main.list_item_phone.view.*


class ContactActivity : AppCompatActivity(), ContactView {

    private lateinit var bindingContact          : ActivityContactBinding
    private lateinit var contactPresenter        : ContactPresenter
    private lateinit var viewModel               : ContactActivityViewModel
    private lateinit var parentPhoneLinearLayout : LinearLayout
    private lateinit var parentMailLinearLayout  : LinearLayout

    companion object {
        val DEFAULT_VALUE_NEW_CONTACT   :Long               = -1

        val DATA_EXISTS                 :Int                = 0
        val DATA_UPDATE                 :Int                = 1
        val DATA_DELETE                 :Int                = 2
        val DATA_CREATE                 :Int                = 3
        val BASE_LIST_SIZE              :Int                = 1

        val EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingContact          =
            DataBindingUtil.setContentView(this, R.layout.activity_contact)
        viewModel               =
            ViewModelProvider(this).get(ContactActivityViewModel::class.java)

        contactPresenter        = ContactPresenter(this)

        parentMailLinearLayout  = listMail
        parentPhoneLinearLayout = listPhone

        viewModel.contactStatusExisting =
            intent.getBooleanExtra(CONTACT_EXISTING_BOOLEAN_EXTRA, false)

        if (viewModel.contactStatusExisting){
            viewModel.editingContact    =
                intent.getSerializableExtra(CONTACT_SERIALIZABLE_EXTRA) as Contact

            with(viewModel.editingContact){
                firstNameInput  .setText(contactFirstName)
                lastNameInput   .setText(contactLastName)
                countryInput    .setText(contactCountryName)

                setEmailPhoneText(contactPhoneNumber, contactEMail)
            }
            saveEditContactBtn.setText(R.string.BTN_EDIT)
        }

        firstNameInput.setOnFocusChangeListener { _, hasFocus ->
            if (viewModel.contactStatusExisting
                && viewModel.editingContact.contactFirstName != firstNameInput.toString()){
                viewModel.editingContact.contactFirstName = firstNameInput.toString()
                viewModel.editingContact.contactEdit = DATA_UPDATE
            }
            errorHandler(
                firstNameTxt,
                !hasFocus && firstNameInput.text!!.count() < 3
            )
        }

        lastNameInput.setOnFocusChangeListener{ _, hasFocus ->
            if (viewModel.contactStatusExisting
                && viewModel.editingContact.contactLastName != lastNameInput.toString()){
                viewModel.editingContact.contactLastName = lastNameInput.toString()
                viewModel.editingContact.contactEdit = DATA_UPDATE
            }
            errorHandler(
                lastNameTxt,
                !hasFocus && lastNameInput.text!!.count() < 3
            )
        }

        countryInput.setOnClickListener { contactPresenter.getCountryNames() }

        phoneInput.setOnFocusChangeListener{ _, hasFocus ->
            if (viewModel.contactStatusExisting) viewModel.phoneLinearLayoutFlag = false
            errorHandler(
                phoneTxt,
                !hasFocus && phoneInput.text!!.count() !in 11..14
            )
        }

        eMailInput.setOnFocusChangeListener { _, hasFocus ->
            if (viewModel.contactStatusExisting) viewModel.phoneLinearLayoutFlag = false
            errorHandler(
                emailTxt,
                !hasFocus && !Validator.EMAIL_REGEX.toRegex()
                    .matches(eMailInput.text.toString())
            )
        }
    }

    fun saveContact(view: View){
        if (viewModel.contactStatusExisting){
            val contact : Contact
            with(viewModel.editingContact){
                contact = Contact(
                    contactID,
                    contactFirstName,
                    contactLastName,
                    contactCountryName,
                    if (!viewModel.phoneLinearLayoutFlag) comparePhonesData() else contactPhoneNumber,
                    if (!viewModel.emailLinearLayoutFlag) compareEmailsData() else contactEMail,
                    contactLocalStorageStats,
                    contactBlob,
                    contactEdit
                )}
            contactPresenter.editContact(contact)
        } else {
            contactPresenter.saveContact(getContactData())
        }
    }

    fun onPhoneLayoutDelete(view: View) {
        viewModel.phoneLinearLayoutFlag = false
        parentPhoneLinearLayout.removeView(view.parent as View)
    }

    fun onPhoneLayoutAdd(view: View?){
        viewModel.phoneLinearLayoutFlag = false
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item_phone, null)

        rowView.phoneInput.setOnFocusChangeListener { _ , hasFocus ->
            if (!hasFocus && rowView.phoneInput.text!!.count() !in 11..14) {
                rowView.phoneTxt.error = getString(R.string.MSG_ENTER_VALID_PHONE_NUMBER)
            }
        }
        parentPhoneLinearLayout.addView(rowView)
    }

    fun onMailLayoutDelete(view: View) {
        viewModel.emailLinearLayoutFlag = false
        parentMailLinearLayout.removeView(view.parent as View)
    }

    fun onMailLayoutAdd(view: View?){
        viewModel.emailLinearLayoutFlag = false
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item_email, null)
        rowView.eMailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus  && !Validator.EMAIL_REGEX.toRegex().matches(rowView.eMailInput.text.toString()))
                rowView.emailTxt.error = getString(R.string.MSG_ENTER_VALID_EMAIL_ADDRESS)
        }
        parentMailLinearLayout.addView(rowView)
    }

    private fun getContactData() :Contact{
        return Contact(
            contactID                   = DEFAULT_VALUE_NEW_CONTACT,
            contactFirstName            = firstNameInput.text.toString(),
            contactLastName             = lastNameInput.text.toString(),
            contactCountryName          = countryInput.text.toString(),
            contactPhoneNumber          = getPhoneData(),
            contactEMail                = getEmailData(),
            contactLocalStorageStats    = true,
            contactBlob                 = null
        )
    }

    private fun getEmailData(): List<ContactEmail>{
        val contactEmails           = mutableListOf<ContactEmail>()
        for (i in parentMailLinearLayout.size - 1 downTo 0) {
            contactEmails.add(ContactEmail(
                contactEmailId          = null,
                contactId               = viewModel.editingContact.contactID,
                contactEmailType        =
                parentMailLinearLayout.getChildAt(i).typeEmail.selectedItem.toString(),
                email                   =
                parentMailLinearLayout.getChildAt(i).eMailInput.text.toString(),
                emailEdit               = DATA_CREATE
            ))
        }
        return contactEmails
    }

    private fun getPhoneData(): List<ContactPhone>{
        val contactPhones           = mutableListOf<ContactPhone>()
        for (i in parentPhoneLinearLayout.size - 1 downTo 0) {
            contactPhones.add(ContactPhone(
                contactPhoneId          = null,
                contactId               = viewModel.editingContact.contactID,
                phone                   =
                parentPhoneLinearLayout.getChildAt(i).phoneInput.text.toString(),
                contactPhoneType        =
                parentPhoneLinearLayout.getChildAt(i).typePhone.selectedItem.toString(),
                phoneEdit               = DATA_CREATE
            ))
        }
        return contactPhones
    }

    private fun compareEmailsData () : List<ContactEmail>{
        val list            = mutableListOf<ContactEmail>()
        val existsList      = mutableListOf<ContactEmail>()
        val newList         = mutableListOf<ContactEmail>()

        existsList          .addAll(viewModel.editingContact.contactEMail)
        newList             .addAll(getEmailData())
        list                .addAll(newList)

        if (existsList.size == BASE_LIST_SIZE && newList.size == BASE_LIST_SIZE){
            list.addAll(existsList)
        }else {
            for (index in existsList.indices) {
                existsList[index].emailEdit = DATA_DELETE
                for (i in newList.indices) {
                    if (existsList[index].email == newList[i].email
                        && existsList[index].contactEmailType == newList[i].contactEmailType) {
                        existsList[index].emailEdit = DATA_EXISTS
                        list.removeAt(i)
                    } else if (existsList[index].email == newList[i].email) {
                        existsList[index].emailEdit = DATA_UPDATE
                        list.removeAt(i)
                    } else {
                        newList[i].emailEdit = DATA_CREATE
                        newList[i].contactId = existsList[index].contactId
                    }
                }
            }
            list.addAll(existsList)
        }
        return list
    }

    private fun comparePhonesData (): List<ContactPhone>{
        val list            = mutableListOf<ContactPhone>()
        val existsList      = mutableListOf<ContactPhone>()
        val newList         = mutableListOf<ContactPhone>()

        existsList          .addAll(viewModel.editingContact.contactPhoneNumber)
        newList             .addAll(getPhoneData())
        list                .addAll(existsList)
        if (existsList.size == 1 && newList.size == 1){
            list.addAll(existsList)
        }else {
            for (index in existsList.indices) {
                existsList[index].phoneEdit = DATA_DELETE
                for (i in newList.indices) {
                    if (existsList[index].phone == newList[i].phone
                        && existsList[index].contactPhoneType == newList[i].contactPhoneType) {
                        existsList[index].phoneEdit = DATA_EXISTS
                        list.removeAt(i)
                    } else if (existsList[index].phone == newList[i].phone) {
                        existsList[index].phoneEdit = DATA_UPDATE
                        list.removeAt(i)
                    } else {
                        newList[i].phoneEdit = DATA_CREATE
                        newList[i].contactId = existsList[index].contactId
                    }
                }
            }
            list.addAll(existsList)
        }
        return list
    }

    private fun setEmailPhoneText(phones: List<ContactPhone>, emails :List<ContactEmail>){
        val phoneSize = phones.size
        if (phoneSize <= 1){
            phoneInput  .setText(phones[phoneSize-1].phone)
            typePhone   .setSelection(selectedType(phones[phoneSize-1].contactPhoneType))
        } else {
            for (index in 0 until phoneSize-1){
                onPhoneLayoutAdd(null)
            }
            for (index in 0 until phoneSize){
                parentPhoneLinearLayout.getChildAt(index).
                    phoneInput.setText(phones[index].phone)
                parentPhoneLinearLayout.getChildAt(index).
                    typePhone.setSelection(selectedType(phones[index].contactPhoneType))
            }
        }
        val emailSize = emails.size
        if (emailSize <= 1){
            eMailInput.setText(emails[emailSize-1].email)
            typeEmail.setSelection(selectedType(emails[emailSize-1].contactEmailType))
        } else {
            for (index in 0 until emailSize-1){
                onMailLayoutAdd(null)
            }
            for (index in 0 until emailSize){
                parentMailLinearLayout.getChildAt(index).
                    eMailInput.setText(emails[index].email)
                parentMailLinearLayout.getChildAt(index).
                    typeEmail.setSelection(selectedType(emails[index].contactEmailType))
            }
        }
    }

    private fun selectedType(type: String) : Int{
        return when (type){
            "Home"      -> 0
            "Mobile"    -> 1
            "Work"      -> 2
            else        -> 3
        }
    }

    private fun errorHandler(textLayout: TextInputLayout, condition: Boolean) {
        if(condition){
            textLayout.setErrorEnabled(true)
            textLayout.error = getString(
                when (textLayout) {
                    firstNameTxt    -> R.string.MSG_ENTER_VALID_FIRST_NAME
                    lastNameTxt     -> R.string.MSG_ENTER_VALID_LAST_NAME
                    emailTxt        -> R.string.MSG_ENTER_VALID_EMAIL_ADDRESS
                    phoneTxt        -> R.string.MSG_ENTER_VALID_PHONE_NUMBER
                    else            -> R.string.MSG_ENTER_COUNTRY
                }
            )
        } else {
            textLayout.setErrorEnabled(false)
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
            countryInput.setText(listCountryRenew[position].countryName)
            errorHandler(countryInputLayout, false)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun toastMsg(result: Int) {
        var message: String
        when(result){
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

    override fun navigateToMainActivity(result: Boolean) {
        val message = getString(R.string.MSG_SAVE_SUCCESSFUL)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("result", result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
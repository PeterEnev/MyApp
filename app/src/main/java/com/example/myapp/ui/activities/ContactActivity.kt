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
import kotlinx.android.synthetic.main.contact_dialog.*
import kotlinx.android.synthetic.main.country_search_dialog.*
import kotlinx.android.synthetic.main.list_item_email.view.*
import kotlinx.android.synthetic.main.list_item_phone.view.*


class ContactActivity : AppCompatActivity(), ContactView {

    private lateinit var bindingContact          : ActivityContactBinding
    private lateinit var contactPresenter        : ContactPresenter
    private lateinit var viewModel               : ContactActivityViewModel

    companion object {
        val DEFAULT_VALUE_NEW_CONTACT   :Long               = -1

        val FIRST_NAME_ID               :Int                = 1
        val LAST_NAME_ID                :Int                = 2
        val COUNTRY_NAME_ID             :Int                = 3
        val PHONE_NUMBER_ID             :Int                = 4
        val PHONE_TYPE_ID               :Int                = 5
        val EMAIL_ID                    :Int                = 6
        val EMAIL_TYPE_ID               :Int                = 7
        val TYPE_OPERATION_UPDATE       :Int                = 11
        val TYPE_OPERATION_DELETE       :Int                = 12
        val TYPE_OPERATION_NEW          :Int                = 13

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

        viewModel.parentMailLinearLayout  = listMail
        viewModel.parentPhoneLinearLayout = listPhone


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
            errorHandler(
                firstNameTxt,
                !hasFocus && firstNameInput.text!!.count() < 5
            )
        }

        lastNameInput.setOnFocusChangeListener{ _, hasFocus ->
            errorHandler(
                lastNameTxt,
                !hasFocus && lastNameInput.text!!.count() < 5
            )
        }

        countryInput.setOnClickListener { contactPresenter.getCountryNames() }

        phoneInput.setOnFocusChangeListener{ _, hasFocus ->
            errorHandler(
                phoneTxt,
                !hasFocus && phoneInput.text!!.count() !in 11..14
            )
        }

        eMailInput.setOnFocusChangeListener { _, hasFocus ->
            errorHandler(
                emailTxt,
                !hasFocus && !Validator.EMAIL_REGEX.toRegex()
                    .matches(eMailInput.text.toString())
            )
        }
    }

    fun saveContact(view: View){

        if (viewModel.contactStatusExisting){
            compareData()
        }else{
            contactPresenter.saveContact(getContactData())
        }
    }

    fun onPhoneLayoutDelete(view: View) {
        listPhone.removeView(view.parent as View)
    }

    fun onPhoneLayoutAdd(view: View?){
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item_phone, null)
        listPhone.addView(rowView, listPhone.childCount - 1)
    }

    fun onMailLayoutDelete(view: View) {
        listMail.removeView(view.parent as View)
    }

    fun onMailLayoutAdd(view: View?){
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item_email, null)
        listMail.addView(rowView, listMail.childCount - 1)
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
        for (i in viewModel.parentMailLinearLayout.size - 1 downTo 0) {
            contactEmails.add(ContactEmail(
                contactEmailId          = null,
                contactId               = null,
                contactEmailType
                = viewModel.parentMailLinearLayout.getChildAt(i).typeEmail.selectedItem.toString(),
                email
                = viewModel.parentMailLinearLayout.getChildAt(i).eMailInput.text.toString()
            ))
        }
        return contactEmails
    }

    private fun getPhoneData(): List<ContactPhone>{
        val contactPhones           = mutableListOf<ContactPhone>()
        for (i in viewModel.parentPhoneLinearLayout.size - 1 downTo 0) {
            contactPhones.add(ContactPhone(
                contactPhoneId          = null,
                contactId               = null,
                phone                   =
                viewModel.parentPhoneLinearLayout.getChildAt(i).phoneInput.text.toString(),
                contactPhoneType        =
                viewModel.parentPhoneLinearLayout.getChildAt(i).typePhone.selectedItem.toString()
            ))
        }
        return contactPhones
    }

    private fun compareData (){
        var list                    = mutableListOf<UpdateData>()
        val id                      = viewModel.editingContact.contactID!!
        val firstName               = firstNameInput.text.toString()
        val lastName                = lastNameInput.text.toString()
        val countryName             = countryInput.text.toString()
        val contactEmails           = getEmailData()
        val contactPhones           = getPhoneData()

        if (firstName != viewModel.editingContact.contactFirstName){
            list.add(UpdateData(
                FIRST_NAME_ID, TYPE_OPERATION_UPDATE, id, firstName
            ))
        }
        if (lastName != viewModel.editingContact.contactLastName){
            list.add(UpdateData(
                LAST_NAME_ID,TYPE_OPERATION_UPDATE, id, lastName
            ))
        }
        if (countryName != viewModel.editingContact.contactCountryName){
            list.add(UpdateData(
                COUNTRY_NAME_ID,TYPE_OPERATION_UPDATE, id, countryName
            ))
        }


        if (list.size != 0) contactPresenter.editContact(list) else navigateToMainActivity(false)
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
                viewModel.parentPhoneLinearLayout.getChildAt(index).
                    phoneInput.setText(phones[index].phone)
                viewModel.parentPhoneLinearLayout.getChildAt(index).
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
                viewModel.parentMailLinearLayout.getChildAt(index).
                    eMailInput.setText(emails[index].email)
                viewModel.parentMailLinearLayout.getChildAt(index).
                    typeEmail.setSelection(selectedType(emails[index].contactEmailType))
            }
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

    fun selectedType(type: String) : Int{
        return when (type){
            "Home"      -> 0
            "Mobile"    -> 1
            "Work"      -> 2
            else        -> 3
        }
    }
}

//    override fun saveContactDialog(contact: Contact) {
//        val dialog = Dialog(this)
//        with(dialog){
//            setCancelable(false)
//            setTitle(getString(R.string.CONTACT_DIALOG_TITLE))
//            setContentView(R.layout.contact_dialog)
//            setCanceledOnTouchOutside(true)
//
//            dialogContactNameTxt.setText(
//                contact.contactFirstName + " " + contact.contactLastName
//            )
//                .toString()
////            dialogContactMailTxt.setText(
////                contact.contactEMail
////            )
////                .toString()
//            dialogContactCountryTxt.setText(
//                contact.contactCountryName
//            )
//                .toString()
//
//            if (contact.contactID == null){
//                dialogEditBtn.setText(getString(R.string.BTN_SAVE))
//            }else{
//                dialogEditBtn.setText(getString(R.string.BTN_UPDATE))
//            }
//
//            dialogCancelBtn.setOnClickListener { dialog.dismiss() }
//
//            dialogEditBtn.setOnClickListener {
//                if (contact.contactID != null){
//                    dismiss()
//                    contactPresenter.saveContact(contact)
//                }else{
//                    dismiss()
//                    contactPresenter.editContact(contact)
//                }
//            }
//            show()
//        }
//    }


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
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapp.R
import com.example.myapp.databinding.ActivityContactBinding
import com.example.myapp.models.*
import com.example.myapp.presenters.ContactPresenter
import com.example.myapp.presenters.ContactView
import com.example.myapp.ui.adapters.ContactListAdapter
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
    private lateinit var parentMailLinearLayout  : LinearLayout
    private lateinit var parentPhoneLinearLayout : LinearLayout

    companion object {
        val CONTACT                     :String             = "contact"
        val DEFAULT_VALUE_NEW_CONTACT   :Long               = -1
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
        bindingContact = DataBindingUtil.setContentView(this, R.layout.activity_contact)

        viewModel = ViewModelProvider(this).get(ContactActivityViewModel::class.java)

        parentMailLinearLayout  = listMail
        parentPhoneLinearLayout = listPhone



        contactPresenter = ContactPresenter(this)

        val contactStatus  = intent.getBooleanExtra(ContactListAdapter.CONTACT_STATUS, false)
        var contactId       = DEFAULT_VALUE_NEW_CONTACT

        if (!ContactListAdapter.CONTACT_STATUS_EXISTING) {
            val editContact = intent.getSerializableExtra(CONTACT) as? Contact
            contactId               = editContact!!.contactID
            firstNameInput  .setText(editContact.contactFirstName)
            lastNameInput   .setText(editContact.contactLastName)
            countryInput    .setText(editContact.contactCountryName)
            phoneTxt        .setPrefixText(editContact.contactCountryPrefix)
            phoneInput      .setText(editContact.contactPhoneNumber)
        //    eMailInput      .setText(editContact.contactEMail)
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


//        saveEditContactBtn.setOnClickListener {
//            if (countryInput.text!!.count() != 0) {
//                var contact =  Contact(
//                    contactFirstName                = firstNameInput.text.toString(),
//                    contactLastName                 = lastNameInput.text.toString(),
//                    contactCountryName              = countryInput.text.toString(),
//                    contactCountryPrefix            = phoneTxt.getPrefixText().toString(),
//                    contactPhoneNumber              = phoneInput.text.toString(),
//                    contactEMail                    = eMailInput.text.toString(),
//                    contactLocalStorageStats        = if (ContactListAdapter.CONTACT_STATUS_EXISTING)
//                                                        contactStatus else false,
//                    contactID                       = contactId
//                )
//                contactPresenter.saveContactDialog(contact)
//            } else {
//                errorHandler(countryInputLayout, true)
//                errorHandler(firstNameTxt, firstNameInput.text!!.count() < 5)
//                errorHandler(lastNameTxt,lastNameInput.text!!.count() < 5)
//                errorHandler(phoneTxt,phoneInput.text!!.count() !in 8..10)
//                errorHandler(emailTxt,!Validator.EMAIL_REGEX.toRegex()
//                        .matches(eMailInput.text.toString()))
//               // errorHandler(genderTxt,(genderInput.text!!.count() < 2))
//            }
//        }

    }

    fun giveData(view: View) {

        for (i in parentMailLinearLayout.size-1 downTo 0) {
            val contactEmail = ContactEmail(
                contactEmailId = null,
                contactId = null,
                contactEmailType = parentMailLinearLayout.getChildAt(i).typeEmail.selectedItem.toString(),
                email = parentMailLinearLayout.getChildAt(i).eMailInput.text.toString()
            )
//            println("${i} -> ${parentMailLinearLayout.getChildAt(i).typeEmail.selectedItem}  " +
//                    "-> ${parentMailLinearLayout.getChildAt(i).eMailInput.text}")
        }

        for (i in parentPhoneLinearLayout.size-1 downTo 0) {
            val contactPhone = ContactPhone(
                contactPhoneId     =null,
                contactId = null,
                phone = parentPhoneLinearLayout.getChildAt(i).phoneInput.text.toString(),
                contactPhoneType = parentPhoneLinearLayout.getChildAt(i).typePhone.selectedItem.toString()
            )
//            println("${i} -> ${parentPhoneLinearLayout.getChildAt(i).typePhone.selectedItem}  " +
//                    "-> ${parentPhoneLinearLayout.getChildAt(i).phoneInput.text}")
        }
    }

    fun onPhoneLayoutDelete(view: View) {
        listPhone.removeView(view.parent as View)
    }

    fun onPhoneLayoutAdd(view: View){
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item_phone, null)
        listPhone.addView(rowView, listPhone.childCount - 1)
    }

    fun onMailLayoutDelete(view: View) {
        listMail.removeView(view.parent as View)
    }

    fun onMailLayoutAdd(view: View){
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item_email, null)
        listMail.addView(rowView, listMail.childCount - 1)
    }


    fun errorHandler(textLayout: TextInputLayout, condition: Boolean) {
        if(condition){
            textLayout.setErrorEnabled(true)
            textLayout.error = getString(
                when (textLayout) {
                    firstNameTxt -> R.string.MSG_ENTER_VALID_FIRST_NAME
                    lastNameTxt -> R.string.MSG_ENTER_VALID_LAST_NAME
                    //   genderTxt       -> R.string.MSG_ENTER_VALID_GENDER
 //                   emailTxt -> R.string.MSG_ENTER_VALID_EMAIL_ADDRESS
                    phoneTxt -> R.string.MSG_ENTER_VALID_PHONE_NUMBER
                    else -> R.string.MSG_ENTER_COUNTRY
                }
            )
        } else {
            textLayout.setErrorEnabled(false)
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
                adapter.updateList(listCountryRenew)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listCountryRenew = ArrayList()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        dialog.countryListView.setOnItemClickListener { _, _, position, _ ->
            countryInput.setText(listCountryRenew[position].countryName)
          //  phoneTxt.setPrefixText(listCountryRenew[position].countryPrefix)
            errorHandler(countryInputLayout, false)
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
//        dialog.dialogContactGenderTxt.setText(
//            contact.contactGender
//        )
//            .toString()

        if (contact.contactID < 0){
            dialog.dialogEditBtn.setText(getString(R.string.BTN_SAVE))
        }else{
            dialog.dialogEditBtn.setText(getString(R.string.BTN_UPDATE))
        }

        dialog.dialogCancelBtn.setOnClickListener { dialog.dismiss() }

        dialog.dialogEditBtn.setOnClickListener {
            if (contact.contactID < 0){
                dialog.dismiss()
                contactPresenter.saveContact(contact)
            }else{
                dialog.dismiss()
                contactPresenter.editContact(contact)
            }
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

    override fun navigateToMainActivity(result: Boolean, contact: Contact) {
        val message = getString(R.string.MSG_SAVE_SUCCESSFUL)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("result", result)
        intent.putExtra(
            "contact",
            Contact(
                contactID = contact.contactID,
                contactFirstName = contact.contactFirstName,
                contactLastName = contact.contactLastName,
                contactCountryName = contact.contactCountryName,
                contactCountryPrefix = contact.contactCountryPrefix,
                contactPhoneNumber = contact.contactPhoneNumber,
                contactEMail = contact.contactEMail,
                // contactGender               = contact.contactGender,
                contactLocalStorageStats = true
            )
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}
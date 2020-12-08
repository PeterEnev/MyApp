package com.example.myapp.ui.activities

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.presenters.MainPresenter
import com.example.myapp.presenters.MainView
import com.example.myapp.ui.adapters.ContactAdapterListener
import com.example.myapp.ui.adapters.ContactListAdapter
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Collections.addAll
import kotlin.contracts.contract

class MainActivity : AppCompatActivity(), MainView, ContactAdapterListener {

    private lateinit var mainPresenter: MainPresenter
    private lateinit var dataContactList: ArrayList<Contact>

    companion object{
        val CONTACT_STATUS                  :String    ="contactStatus"
        val CONTACT_STATUS_NEW              :Boolean   =true
        val EMPTY_STRING                    :String    =""
        val CONTACT_NAME                    :String    ="contact"
        val TOAST_CONTACT_EDITED            :String    ="The contact cannot be edited"
        val DEFAULT_VALUE_PHONE_CONTACT     :Long      =-2
        val STORAGE_PERMISSION_CODE         :Int       = 1
        val REQUEST_CODE_OK                 :Int       = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainPresenter = MainPresenter(this)

        if(ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mainPresenter.setContactList()
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(READ_CONTACTS), STORAGE_PERMISSION_CODE)
        }


        newContactFab.setOnClickListener { mainPresenter.newContactFabClick() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE && ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ){
            mainPresenter.setContactList()
        } else {
            mainPresenter.setContactListNoPermission()
        }
    }

    override fun navigateToNewContactActivity(){
        val intent = Intent(this, ContactActivity::class.java)
        intent.putExtra(CONTACT_STATUS, CONTACT_STATUS_NEW)
        startActivityForResult(intent, REQUEST_CODE_OK)
    }

    override fun setContactList(contactList: ArrayList<Contact>){
        var listContactRenew = contactList
        mainActivityRecyclerView.adapter = ContactListAdapter(listContactRenew, this)
        mainActivityRecyclerView.layoutManager = LinearLayoutManager(this)

        mainActivitySearchContactInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                for (i in 0 until contactList.size){
                    if ((contactList[i].contactFirstName + " " + contactList[i].contactLastName).
                        toUpperCase().contains(s.toString().toUpperCase()) ||
                        (contactList[i].contactCountryPrefix + contactList[i].contactPhoneNumber).contains(s.toString())){
                        listContactRenew.add(contactList[i])
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listContactRenew = ArrayList()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
//  override fun onActivityResult(requestCode: Int, resultCode: Int, intent : Intent?){
//        super.onActivityResult(requestCode, resultCode, intent)
//
////        val contactList = ArrayList<Contact>()
////        val contact = intent?.getSerializableExtra("contact") as Contact
////
////        val req = intent.getBooleanExtra("request", true)
////
////        if (req){
////            contactList.add(0, contact)
////            mainActivityRecyclerViewAdapter(contactList, true)
////        }
////        else{
////            val message = getString(R.string.MSG_OOPS)
////            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
////        }
//    }

    override fun onEditBtnListener(contact: Contact, contactStatus: String, contactStatusExisting: Boolean) {
        if (contactStatusExisting){
            val intent = Intent(this, ContactActivity::class.java)
                    intent.putExtra(CONTACT_STATUS, contactStatusExisting)
                    intent.putExtra(CONTACT_NAME,
                        Contact(
                            contactID                   = contact.contactID,
                            contactFirstName            = contact.contactFirstName,
                            contactLastName             = contact.contactLastName,
                            contactCountryName          = contact.contactCountryName,
                            contactCountryPrefix        = contact.contactCountryPrefix,
                            contactPhoneNumber          = contact.contactPhoneNumber,
                            contactEMail                = contact.contactEMail,
                            contactGender               = contact.contactGender,
                            contactLocalStorageStats    = contact.contactLocalStorageStats))
            startActivityForResult(intent, REQUEST_CODE_OK)
        }else{
            Toast.makeText(this, TOAST_CONTACT_EDITED, Toast.LENGTH_SHORT).show()
        }
    }
}

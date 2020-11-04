package com.example.myapp.ui.activities

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView, ContactAdapterListener {

    private lateinit var mainPresenter: MainPresenter
    companion object{

        val CONTACT_STATUS                  : String    = "contactStatus"
        val CONTACT_STATUS_NEW              : String    = "new"
        val EMPTY_STRING                    : String    = ""
        val CONTACT_NAME                    : String    = "contact"
        val TOAST_CONTACT_EDITED            : String    = "The contact cannot be edited"
        val DEFAULT_VALUE_PHONE_CONTACT     : Long      = -2
        val STORAGE_PERMISSION_CODE         : Int       = 1
        val REQUEST_CODE                    : Int       = 0
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
        }
    }

    override fun navigateToNewContactActivity(){
        val intent = Intent(this, ContactActivity::class.java)
        intent.putExtra(CONTACT_STATUS, CONTACT_STATUS_NEW)
        startActivityForResult(intent, REQUEST_CODE)

    }

    override fun setContactList(contactList: ArrayList<Contact>){
        var listContactRenew = contactList
        var contactListAdapter = ContactListAdapter(listContactRenew, this)

        mainActivityRecyclerView.layoutManager = LinearLayoutManager(this)
        mainActivityRecyclerView.adapter = contactListAdapter

        mainActivitySearchContactInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                for (i in 0 until contactList.size){
                    if ((contactList[i].contactFirstName + " " + contactList[i].contactLastName).
                        toUpperCase().contains(s.toString().toUpperCase()) ||
                        (contactList[i].contactCountryPrefix + contactList[i].contactPhoneNumber).contains(s.toString())){
                        listContactRenew.add(contactList[i])
                    }
                }
                contactListAdapter = ContactListAdapter(listContactRenew, this@MainActivity)
                mainActivityRecyclerView.adapter = contactListAdapter
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listContactRenew = ArrayList()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE){
            finish()
            startActivity(intent)
        }
        else{
            val message = getString(R.string.MSG_OOPS)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditBtnListener(contact: Contact, contactStatus: String, contactStatusExisting: String) {
        if (contact.contactLocalStorageStats){
            val intent = Intent(this, ContactActivity::class.java)
                    intent.putExtra(contactStatus, contactStatusExisting)
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
                    startActivityForResult(intent, REQUEST_CODE)
                }else{
                    Toast.makeText(this, TOAST_CONTACT_EDITED, Toast.LENGTH_SHORT).show()
                }
    }
}

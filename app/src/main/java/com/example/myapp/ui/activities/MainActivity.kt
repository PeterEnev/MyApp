package com.example.myapp.ui.activities


//import android.2.permission.READ_CONTACTS
//import
import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
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
import com.example.myapp.ui.adapters.ContactListAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    val mainPresenter: MainPresenter = MainPresenter(this)

    /*init {
        instance = this
    }*/
    companion object{
        //private var instance: MainActivity? = null

        val CONTACT_STATUS                  : String    = "contactStatus"
        val CONTACT_STATUS_NEW              : String    = "new"
        val EMPTY_STRING                    : String    = ""
        val DEFAULT_VALUE_PHONE_CONTACT     : Long      = -2
        val STORAGE_PERMISSION_CODE         : Int       = 1

        /*fun applicationContext() : Context {
            return instance!!.applicationContext
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
        }else{
            if(Build.VERSION.SDK_INT > 22){
                requestPermissions(arrayOf(READ_CONTACTS), STORAGE_PERMISSION_CODE)
            }
        }
        mainPresenter.setContactList()
        newContactFab.setOnClickListener { mainPresenter.newContactFabClick() }
    }

    override fun navigateToNewContactActivity(){
        val intent = Intent(this, ContactActivity::class.java)
        intent.putExtra(CONTACT_STATUS, CONTACT_STATUS_NEW)
        startActivity(intent)

    }

    override fun setContactList(contactList: ArrayList<Contact>){
        var phoneContact: ArrayList<Contact> = getPhoneContact()
        contactList.addAll(phoneContact)
        var listContactRenew = contactList
        var contactListAdapter = ContactListAdapter(listContactRenew)

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
                contactListAdapter = ContactListAdapter(listContactRenew)
                mainActivityRecyclerView.adapter = contactListAdapter
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listContactRenew = ArrayList()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    fun getPhoneContact(): ArrayList<Contact> {
        var contactPhoneList = ArrayList<Contact>()

        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )


        while (phones.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            println(name)
            val list = Contact(
                contactID                   = DEFAULT_VALUE_PHONE_CONTACT,
                contactFirstName            = name,
                contactLastName             = EMPTY_STRING,
                contactPhoneNumber          = phoneNumber,
                contactEMail                = EMPTY_STRING,
                contactCountryPrefix        = EMPTY_STRING,
                contactCountryName          = EMPTY_STRING,
                contactGender               = EMPTY_STRING,
                contactLocalStorageStats    = false
            )
            println(list.toString())
            contactPhoneList.add(list)
        }
        phones.close()
        return contactPhoneList
    }
}

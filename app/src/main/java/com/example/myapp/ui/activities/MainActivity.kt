package com.example.myapp.ui.activities

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.Database
import com.example.myapp.R
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.models.Contact
import com.example.myapp.presenters.MainPresenter
import com.example.myapp.presenters.MainView
import com.example.myapp.ui.adapters.ContactAdapterListener
import com.example.myapp.ui.adapters.ContactListAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Collections.addAll
import kotlin.contracts.contract

class MainActivity : AppCompatActivity(), MainView, ContactAdapterListener {

    private lateinit var bindingMain        : ActivityMainBinding
    private lateinit var mainPresenter      : MainPresenter
    private lateinit var dataContactList    : ArrayList<Contact>
    private lateinit var adapter            : ContactListAdapter


    companion object{
        val CONTACT_EXISTING_BOOLEAN_EXTRA  :String    = "existing"
        val CONTACT_SERIALIZABLE_EXTRA      :String    = "data"
        val CONTACT_STATUS_NEW              :Boolean   = true
        val CONTACT_STATUS_EXISTING         :Boolean   = false
        val EMPTY_STRING                    :String    = ""
        val DEFAULT_VALUE_PHONE_CONTACT     :Long      = -2
        val STORAGE_PERMISSION_CODE         :Int       =  1
        val REQUEST_CODE_OK                 :Int       =  0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main)


        mainPresenter = MainPresenter(this)

        if(ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mainPresenter.setContactList()
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(READ_CONTACTS), STORAGE_PERMISSION_CODE)
        }


        bindingMain.newContactFab.setOnClickListener { mainPresenter.newContactFabClick() }
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
        startActivityForResult(intent, REQUEST_CODE_OK)
    }

    override fun setContactList(data: ArrayList<Contact>){
        dataContactList = data
        var listContactRenew = data
        adapter = ContactListAdapter(listContactRenew, this)
        mainActivityRecyclerView.adapter = adapter
        mainActivityRecyclerView.layoutManager = LinearLayoutManager(this)

        mainActivitySearchContactInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                for (i in 0 until dataContactList.size){
                    if ((dataContactList[i].contactFirstName + " " + dataContactList[i].contactLastName).
                        toUpperCase().contains(s.toString().toUpperCase())){
                        listContactRenew.add(dataContactList[i])
                    }
                }
                adapter.updateList(listContactRenew)
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
        dataContactList = mainPresenter.getContactList()
        adapter.updateList(dataContactList)
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

    override fun onEditBtnListener(contact: Contact) {
        val intent = Intent(this, ContactActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(CONTACT_SERIALIZABLE_EXTRA, contact)
        intent.putExtras(bundle)
        intent.putExtra(CONTACT_EXISTING_BOOLEAN_EXTRA, true)
        startActivityForResult(intent, REQUEST_CODE_OK)
    }
}

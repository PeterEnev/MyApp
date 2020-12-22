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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.R
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.models.Contact
import com.example.myapp.presenters.MainPresenter
import com.example.myapp.presenters.MainView
import com.example.myapp.ui.adapters.ContactAdapterListener
import com.example.myapp.ui.adapters.ContactListAdapter
import kotlinx.android.synthetic.main.activity_main.*

private const val CONTACT_EXISTING_BOOLEAN_EXTRA        = "existing"
private const val CONTACT_SERIALIZABLE_EXTRA            = "data"
private const val STORAGE_PERMISSION_CODE               =  1
private const val REQUEST_CODE_OK                       =  0

class MainActivity : AppCompatActivity(), MainView, ContactAdapterListener {

    private lateinit var bindingMain                    : ActivityMainBinding
    private lateinit var mainPresenter                  : MainPresenter
    private lateinit var dataContactList                : ArrayList<Contact>
    private lateinit var dataContactListRenew           : ArrayList<Contact>
    private lateinit var adapter                        : ContactListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
        if(requestCode == STORAGE_PERMISSION_CODE
            && ContextCompat.checkSelfPermission(this, READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED ){
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
        dataContactList                         = data
        dataContactListRenew                    = data
        adapter                                 = ContactListAdapter(dataContactListRenew, this, this)
        mainActivityRecyclerView.adapter        = adapter
        mainActivityRecyclerView.layoutManager  = LinearLayoutManager(this)

        mainActivitySearchContactInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                for (i in 0 until dataContactList.size){
                    if ((dataContactList[i].contactFirstName + " " + dataContactList[i].contactLastName).
                        toUpperCase().contains(s.toString().toUpperCase())){
                        dataContactListRenew.add(dataContactList[i])
                    }
                }
                adapter.updateList(dataContactListRenew)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                dataContactListRenew = ArrayList()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dataContactList = mainPresenter.getContactList()
        adapter.updateList(dataContactList)
    }

    override fun onEditBtnListener(contact: Contact) {
        val intent = Intent(this, ContactActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(CONTACT_SERIALIZABLE_EXTRA, contact)
        intent.putExtras(bundle)
        intent.putExtra(CONTACT_EXISTING_BOOLEAN_EXTRA, true)
        startActivityForResult(intent, REQUEST_CODE_OK)
    }

    override fun getContactData(contact: Contact, position: Int): Contact {
        return  mainPresenter.getContact(contact)
    }

    override fun toastErrorMsg() {
        Toast.makeText(this, getString(R.string.MSG_OOPS), Toast.LENGTH_SHORT).show()
    }
}

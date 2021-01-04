package com.example.myapp.presenters

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.myapp.data.DatabaseDB
import com.example.myapp.helperClasses.ContactsData
import com.example.myapp.helperClasses.UtilsDefines
import com.example.myapp.models.*
import com.example.myapp.ui.activities.MainActivity
import kotlinx.coroutines.*

class MainPresenter(private val mainView: MainView) {

    fun setContactList(activity: MainActivity) {
        if(ContextCompat.checkSelfPermission(activity, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
        val contactList = ContactsData().allContactData()
        mainView.setContactList(contactList)
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(arrayOf(READ_CONTACTS), UtilsDefines.STORAGE_PERMISSION_CODE)
        }
    }

    fun setContactListNoPermission() = runBlocking {
        when (val result = withTimeoutOrNull(UtilsDefines.TIME_OUT_IN_MILLISECONDS){ DatabaseDB().getContactList()}){
            null -> mainView.toastErrorMsg(UtilsDefines.CODE_MSG_TIMEOUT)
            else -> mainView.setContactList(result)
        }
    }

    fun newContactFabClick() = mainView.navigateToNewContactActivity()

    fun getContact(contact: Contact) : Contact {
        if (contact.contactPhoneNumber != null && contact.contactEMail != null)
            return contact
        else
            return ContactsData().getContactPhonesAndEmails(contact)
    }
}

interface MainView{
    fun navigateToNewContactActivity()
    fun setContactList(contactList: ArrayList<Contact>)
    fun toastErrorMsg(result: Int)
}
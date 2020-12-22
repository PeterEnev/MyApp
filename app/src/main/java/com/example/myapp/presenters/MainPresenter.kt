package com.example.myapp.presenters

import com.example.myapp.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

private const val TIME_OUT_IN_MILLISECONDS               = 3000L


class MainPresenter(private val mainView: MainView) {

    fun setContactList() {
        val contactList = ContactsData().allContactData()
        mainView.setContactList(contactList)
    }

    fun setContactListNoPermission() = runBlocking {
        when (val result = withTimeoutOrNull(TIME_OUT_IN_MILLISECONDS){DatabaseDB().getContactList()}){
            null -> mainView.toastErrorMsg()
            else -> mainView.setContactList(result)
        }
    }

    fun newContactFabClick(){ mainView.navigateToNewContactActivity() }

    fun getContactList(): ArrayList<Contact> { return  ContactsData().allContactData() }

    fun getContact(contact: Contact): Contact{
        return ContactsData().getContactPhonesAndEmails(contact)
    }
}

interface MainView{
    fun navigateToNewContactActivity()
    fun setContactList(contactList: ArrayList<Contact>)
    fun toastErrorMsg()
}
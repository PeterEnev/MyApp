package com.example.myapp.presenters

import com.example.myapp.models.Contact
import com.example.myapp.models.DatabaseDB
import com.example.myapp.models.PhoneContact
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainPresenter(private val mainView: MainView) {

    fun setContactList() {
        val contactList = getContactList()
        mainView.setContactList(contactList)
    }

    fun setContactListNoPermission() = runBlocking {
        val contactList = async{ DatabaseDB().getContactList() }.await()
        mainView.setContactList(contactList)
    }

    fun newContactFabClick(){
        mainView.navigateToNewContactActivity()
    }

    fun getContactList(): ArrayList<Contact> = runBlocking{
        val contactList = async { DatabaseDB().getContactList() }.await()
        val phoneContactList = async { PhoneContact().getPhoneContact() }.await()
        contactList.addAll(phoneContactList)
        return@runBlocking contactList
    }
}

interface MainView{
    fun navigateToNewContactActivity()
    fun setContactList(contactList: ArrayList<Contact>)
}
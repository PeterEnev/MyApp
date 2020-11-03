package com.example.myapp.presenters

import com.example.myapp.models.Contact
import com.example.myapp.models.DatabaseDB
import com.example.myapp.models.PhoneContact
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainPresenter(private val mainView: MainView) {

    fun setContactList() = runBlocking{
        val contactList = async{ DatabaseDB().getContactList() }.await()
        val phoneContactList = async { PhoneContact().getPhoneContact() }.await()
        contactList.addAll(phoneContactList)
        mainView.setContactList(contactList)
    }

    fun newContactFabClick(){
        mainView.navigateToNewContactActivity()
    }

}

interface MainView{
    fun navigateToNewContactActivity()
    fun setContactList(contactList: ArrayList<Contact>)
}
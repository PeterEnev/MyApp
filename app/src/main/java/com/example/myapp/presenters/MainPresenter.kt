package com.example.myapp.presenters

import com.example.myapp.models.Contact
import com.example.myapp.models.ContactsData
import com.example.myapp.models.DatabaseDB
import com.example.myapp.models.PhoneContact
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

private const val TIME_OUT_IN_MILISECONDS               = 3000L
class MainPresenter(private val mainView: MainView) {

    fun setContactList() {
        val contactList = ContactsData().allContactData()
        mainView.setContactList(contactList)
    }

    fun setContactListNoPermission() = runBlocking {
        when (val result = withTimeoutOrNull(TIME_OUT_IN_MILISECONDS){DatabaseDB().getContactList()}){
            null -> mainView.toastErrorMsg()
            else -> mainView.setContactList(result)
        }
        //val result = async{ DatabaseDB().getContactList() }.await()
    }

    fun newContactFabClick(){
        mainView.navigateToNewContactActivity()
    }

    fun getContactList(): ArrayList<Contact> {
        return  ContactsData().allContactData()
    }
}

interface MainView{
    fun navigateToNewContactActivity()
    fun setContactList(contactList: ArrayList<Contact>)
    fun toastErrorMsg()
}
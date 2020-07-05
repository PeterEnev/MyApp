package com.example.myapp.presenters

import com.example.myapp.models.Contact
import com.example.myapp.models.DatabaseDB
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainPresenter(private val mainView: MainView) {

    fun setContactList() = runBlocking{
        val contactList = async{ DatabaseDB().getContactList()}.await()
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
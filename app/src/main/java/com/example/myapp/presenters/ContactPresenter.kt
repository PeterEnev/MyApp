package com.example.myapp.presenters

import com.example.myapp.Database
import com.example.myapp.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ContactPresenter(private val contactView: ContactView) {


    fun getCountryNames() = runBlocking {
        var listCountry = async { DatabaseDB().getCountryList() }.await()
        contactView.openChoiceCountryDialog(listCountry)
    }

//    fun saveContactDialog(contact: Contact) {
//        var checkResultStatus = 0// Validator().checkContact(contact)
//
//        if (checkResultStatus == 0) {
//            contactView.saveContactDialog(contact)
//        } else {
//            contactView.toastMsg(checkResultStatus)
//        }
//    }

    fun saveContact(contact: Contact) = runBlocking {
        val result = async {  DatabaseDB().saveNewContact(contact) }.await()
        if (result) {
            contactView.navigateToMainActivity(result)
        } else {
            contactView.toastMsg(6)
        }
    }
    fun editContact(list: List<UpdateData>)
            = runBlocking{
        val result = async {  DatabaseDB().updateContact(list) }.await()
        if (result) {
            contactView.navigateToMainActivity(result)
        } else {
            contactView.toastMsg(6)
        }
    }

}

interface ContactView{
    fun openChoiceCountryDialog(listCountry: ArrayList<Country>)
//    fun saveContactDialog(contact: Contact)
    fun navigateToMainActivity(message: Boolean)
    fun toastMsg(checkResultStatus: Int)
}
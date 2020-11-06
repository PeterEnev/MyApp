package com.example.myapp.presenters

import com.example.myapp.models.Contact
import com.example.myapp.models.Country
import com.example.myapp.models.DatabaseDB
import com.example.myapp.models.Validator
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ContactPresenter(private val contactView: ContactView) {


    fun getCountryNames() = runBlocking {
        var listCountry = async { DatabaseDB().getCountryList() }.await()
        contactView.openChoiceCountryDialog(listCountry)
    }

    fun saveContactDialog(contact: Contact) {
        var checkResultStatus = Validator().checkContact(contact)

        if (checkResultStatus == 0) {
            contactView.saveContactDialog(contact)
        } else {
            contactView.toastMsg(checkResultStatus)
        }
    }

    fun saveContact(contact: Contact) = runBlocking {
        val result = async {  DatabaseDB().saveNewContact(contact) }.await()
        if (result == 0) {
            contactView.navigateToMainActivity(result, contact)
        } else {
            contactView.toastMsg(result)
        }
    }
    fun editContact(contact: Contact) = runBlocking{
        val result = async {  DatabaseDB().updateContact(contact) }.await()
        if (result == 0) {
            contactView.navigateToMainActivity(result, contact)
        } else {
            contactView.toastMsg(result)
        }
    }
}

interface ContactView{
    fun openChoiceCountryDialog(listCountry: ArrayList<Country>)
    fun saveContactDialog(contact: Contact)
    fun navigateToMainActivity(message: Int, contact: Contact)
    fun toastMsg(checkResultStatus: Int)
}
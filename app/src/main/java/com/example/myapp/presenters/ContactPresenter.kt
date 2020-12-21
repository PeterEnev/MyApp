package com.example.myapp.presenters

import com.example.myapp.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import javax.xml.transform.Result

private const val TIME_OUT_IN_MILISECONDS               = 3000L
class ContactPresenter(private val contactView: ContactView) {


    fun getCountryNames() = runBlocking {
        var listCountry = async { DatabaseDB().getCountryList() }.await()
        contactView.openChoiceCountryDialog(listCountry)
    }

    fun saveContact(contact: Contact) = runBlocking {
        val checkValid = Validator().checkContact(contact)
        if (checkValid == 0){
            when (val result = withTimeoutOrNull(TIME_OUT_IN_MILISECONDS){DatabaseDB().saveNewContact(contact)}){
                null -> contactView.toastMsg(6)
                else -> contactView.navigateToMainActivity(result)
            }
//            val result = async {  DatabaseDB().saveNewContact(contact) }.await()
//            if (result)
//                contactView.navigateToMainActivity(result)
//            else
//                contactView.toastMsg(6)
        } else {
            contactView.toastMsg(checkValid)
        }

    }
    fun editContact(contact: Contact) = runBlocking{
        val checkValid = Validator().checkContact(contact)
        if (checkValid == 0) {
            when (val result = withTimeoutOrNull(TIME_OUT_IN_MILISECONDS){ContactsData().updateContactData(contact)}){
                null -> contactView.toastMsg(6)
                else -> contactView.navigateToMainActivity(result)
            }
//            val result = async { ContactsData().updateContactData(contact) }.await()
//            if (result)
//                contactView.navigateToMainActivity(result)
//            else
//                contactView.toastMsg(6)
        }else {
            contactView.toastMsg(checkValid)
        }

    }

}

interface ContactView{
    fun openChoiceCountryDialog(listCountry: ArrayList<Country>)
    fun navigateToMainActivity(message: Boolean)
    fun toastMsg(checkResultStatus: Int)
}
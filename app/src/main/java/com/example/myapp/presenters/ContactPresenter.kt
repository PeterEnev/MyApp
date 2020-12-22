package com.example.myapp.presenters

import com.example.myapp.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import javax.xml.transform.Result

private const val TIME_OUT_IN_MILLISECONDS              = 3000L
private const val CODE_DATA_VALID                       = 0
private const val CODE_MSG_TIMEOUT                      = 6

class ContactPresenter(private val contactView: ContactView) {

    fun getCountryNames() = runBlocking {
        var listCountry = async { DatabaseDB().getCountryList() }.await()
        contactView.openChoiceCountryDialog(listCountry)
    }

    fun saveContact(contact: Contact) = runBlocking {
        val checkValid = Validator().checkContact(contact)
        if (checkValid == CODE_DATA_VALID){
            when (val result = withTimeoutOrNull(TIME_OUT_IN_MILLISECONDS){DatabaseDB().saveNewContact(contact)}){
                null -> contactView.toastMsg(CODE_MSG_TIMEOUT)
                else -> contactView.navigateToMainActivity(result)
            }
        } else {
            contactView.toastMsg(checkValid)
        }
    }

    fun editContact(contact: Contact) = runBlocking{
        val checkValid = Validator().checkContact(contact)
        if (checkValid == CODE_DATA_VALID) {
            when (val result = withTimeoutOrNull(TIME_OUT_IN_MILLISECONDS){ContactsData().updateContactData(contact)}){
                null -> contactView.toastMsg(CODE_MSG_TIMEOUT)
                else -> contactView.navigateToMainActivity(result)
            }
        } else {
            contactView.toastMsg(checkValid)
        }
    }
}

interface ContactView{
    fun openChoiceCountryDialog(listCountry: ArrayList<Country>)
    fun navigateToMainActivity(message: Boolean)
    fun toastMsg(checkResultStatus: Int)
}
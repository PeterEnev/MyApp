package com.example.myapp.presenters

import com.example.myapp.data.DatabaseDB
import com.example.myapp.helperClasses.ContactsData
import com.example.myapp.helperClasses.UtilsDefines
import com.example.myapp.helperClasses.Validator
import com.example.myapp.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

class ContactPresenter(private val contactView: ContactView) {

    fun getCountryNames() = runBlocking {
        var listCountry = async { DatabaseDB().getCountryList() }.await()
        contactView.openChoiceCountryDialog(listCountry)
    }

    fun saveContact(contact: Contact) = runBlocking {
        val checkValid = Validator().checkContact(contact)
        if (checkValid == UtilsDefines.CODE_DATA_VALID){
            when (val result = withTimeoutOrNull(UtilsDefines.TIME_OUT_IN_MILLISECONDS){ DatabaseDB().saveNewContact(contact)}){
                null -> contactView.toastMsg(UtilsDefines.CODE_MSG_TIMEOUT)
                else -> contactView.navigateToMainActivity(if (result) checkValid else UtilsDefines.CODE_MSG_OPS)
            }
        } else {
            contactView.toastMsg(checkValid)
        }
    }

    fun editContact(contact: Contact) = runBlocking{
        val checkValid = Validator().checkContact(contact)
        if (checkValid == UtilsDefines.CODE_DATA_VALID) {
            when (val result = withTimeoutOrNull(UtilsDefines.TIME_OUT_IN_MILLISECONDS){ ContactsData().updateContactData(contact)}){
                null -> contactView.toastMsg(UtilsDefines.CODE_MSG_TIMEOUT)
                else -> contactView.navigateToMainActivity(if (result) checkValid else UtilsDefines.CODE_MSG_OPS)
            }
        } else {
            contactView.toastMsg(checkValid)
        }
    }
}

interface ContactView{
    fun openChoiceCountryDialog(listCountry: ArrayList<Country>)
    fun navigateToMainActivity(message: Int)
    fun toastMsg(checkResultStatus: Int)
}
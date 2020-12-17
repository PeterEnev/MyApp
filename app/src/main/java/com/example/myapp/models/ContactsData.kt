package com.example.myapp.models

import com.example.myapp.Database
import com.example.myapp.ui.activities.ContactActivity.Companion.DATA_CREATE
import com.example.myapp.ui.activities.ContactActivity.Companion.DATA_DELETE
import com.example.myapp.ui.activities.ContactActivity.Companion.DATA_EXISTS
import com.example.myapp.ui.activities.ContactActivity.Companion.DATA_UPDATE
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class ContactsData : DatabaseDB(){

    fun allContactData(): ArrayList<Contact> = runBlocking {
        val contactList         = arrayListOf<Contact>()
        val phoneContact        = async { PhoneContact().getPhoneContact()  }.await()
        val databaseContact     = async { getContactList()     }.await()

        contactList.addAll(databaseContact)


        for (index in 0 until phoneContact.size){
            if(!compareContact(databaseContact, phoneContact[index].contactFirstName))
                contactList.add(phoneContact[index])
        }
        return@runBlocking contactList
    }

    private fun compareContact(list: ArrayList<Contact>, name: String) : Boolean{
        var result = false
        loop@ for (index in 0 until list.size){
            if((list[index].contactFirstName + " " + list[index].contactLastName).contains(name)) {
                result = true
                break@loop
            }
        }
        return result
    }

    fun updateContactData(contact: Contact) : Boolean{
        var result = false
        database.transaction {
            if(contact.contactEdit != DATA_EXISTS){
                updateContact(contact)
                result = true
            }
            phoneLoop@for (index in contact.contactPhoneNumber.indices){
                result = true
                if (contact.contactPhoneNumber[index].phoneEdit == DATA_EXISTS)
                    break@phoneLoop
                else if (contact.contactPhoneNumber[index].phoneEdit == DATA_CREATE)
                    addPhone(contact.contactPhoneNumber[index])
                else if (contact.contactPhoneNumber[index].phoneEdit == DATA_UPDATE)
                    updatePhone(contact.contactPhoneNumber[index])
                else if (contact.contactPhoneNumber[index].phoneEdit == DATA_DELETE)
                    deletePhone(contact.contactPhoneNumber[index])
                else result = false
            }
            emailLoop@for (index in contact.contactEMail.indices){
                result = true
                if (contact.contactEMail[index].emailEdit == DATA_EXISTS)
                    break@emailLoop
                else if (contact.contactEMail[index].emailEdit == DATA_CREATE)
                    addEmail(contact.contactEMail[index])
                else if (contact.contactEMail[index].emailEdit == DATA_UPDATE)
                    updateEmail(contact.contactEMail[index])
                else if (contact.contactEMail[index].emailEdit == DATA_DELETE)
                    deleteEmail(contact.contactEMail[index])
                else result = false
            }
        }
        return result
    }
}
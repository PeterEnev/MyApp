package com.example.myapp.helperClasses

import com.example.myapp.models.Contact
import com.example.myapp.data.DatabaseDB
import com.example.myapp.data.PhoneContact
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

private const val DATA_EXISTS                       = 0
private const val DATA_UPDATE                       = 1
private const val DATA_DELETE                       = 2
private const val DATA_CREATE                       = 3
private const val EMPTY_STRING                      = " "

class ContactsData : DatabaseDB(){

    fun getContactPhonesAndEmails(contact: Contact): Contact {

        if (contact.contactLocalStorageStats) {
            database.transaction {
                contact.contactPhoneNumber  = getContactPhones(contact.contactID!!)
                contact.contactEMail        = getContactEmails(contact.contactID!!)
            }
            return contact
        } else {
            return PhoneContact().getContactData(contact)
        }
    }

    fun allContactData(): ArrayList<Contact> = runBlocking {
        val contactList         = arrayListOf<Contact>()
        val phoneContact        = async { PhoneContact().getAllNameAndPhoto()  }.await()
        val databaseContact     = async { getContactList()                     }.await()

        contactList.addAll(databaseContact)

        for (index in phoneContact.indices){
            if(!compareContact(databaseContact, phoneContact[index].contactFirstName))
                contactList.add(phoneContact[index])
        }
        return@runBlocking contactList
    }

    fun updateContactData(contact: Contact) : Boolean{
        var result = false
        database.transaction {
            if(contact.contactEdit != DATA_EXISTS) {
                updateContact(contact)
                result = true
            }
            for (index in contact.contactPhoneNumber!!.indices) {
                if (contact.contactPhoneNumber!![index].phoneEdit == DATA_CREATE) {
                    addPhone(contact.contactPhoneNumber!![index])
                    result = true
                } else if (contact.contactPhoneNumber!![index].phoneEdit == DATA_UPDATE) {
                    updatePhone(contact.contactPhoneNumber!![index])
                    result = true
                } else if (contact.contactPhoneNumber!![index].phoneEdit == DATA_DELETE) {
                    deletePhone(contact.contactPhoneNumber!![index])
                    result = true
                }
            }
            for (index in contact.contactEMail!!.indices) {
                if (contact.contactEMail!![index].emailEdit == DATA_CREATE) {
                    addEmail(contact.contactEMail!![index])
                    result = true
                } else if (contact.contactEMail!![index].emailEdit == DATA_UPDATE) {
                    updateEmail(contact.contactEMail!![index])
                    result = true
                } else if (contact.contactEMail!![index].emailEdit == DATA_DELETE) {
                    deleteEmail(contact.contactEMail!![index])
                    result = true
                }
            }
        }
        return result
    }

    private fun compareContact(list: ArrayList<Contact>, name: String) : Boolean{
        var result = false
        for (index in list.indices){
            if((list[index].contactFirstName + EMPTY_STRING + list[index].contactLastName) == name) {
                result = true
                break
            }
        }
        return result
    }
}
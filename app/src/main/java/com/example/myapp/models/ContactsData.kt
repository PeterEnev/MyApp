package com.example.myapp.models

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ContactsData{

    fun allContactData(): ArrayList<Contact> = runBlocking {
        val contactList         = arrayListOf<Contact>()
        val phoneContact        = async { PhoneContact().getPhoneContact() }.await()
        val databaseContact     = async {DatabaseDB().getContactList() }.await()

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
}
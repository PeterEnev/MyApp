package com.example.myapp.models

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.myapp.ContactsDBQueries
import com.example.myapp.CountyiesDBQueries
import com.example.myapp.Database
import com.example.myapp.ui.activities.MainActivity
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.delay

class DatabaseDB {
    val context             : Context               = MainActivity.applicationContext()
    val databaseName        : String                = "test.db"
    val driver              : SqlDriver             = AndroidSqliteDriver(Database.Schema, context, databaseName)
    val database            : Database              = Database(driver)
    val dbContriesQuery     : CountyiesDBQueries    = database.countyiesDBQueries
    val dbContactsQuery     : ContactsDBQueries     = database.contactsDBQueries


    fun saveNewContact(contact: Contact) : Int{
        val status = 0
        dbContactsQuery.insertContacts(
            contactFirstName    = contact.contactFirstName,
            contactLastName     = contact.contactLastName,
            countryName         = contact.contactCountryName,
            contactEMail        = contact.contactEMail,
            contactPhoneNumber  = contact.contactPhoneNumber,
            contactGender       = contact.contactGender
        )
        return status
    }

    fun updateContact(contact: Contact) : Int{
        val status = 0
        dbContactsQuery.updateContacts(
            contactFirstName    = contact.contactFirstName,
            contactLastName     = contact.contactLastName,
            countryName         = contact.contactCountryName,
            contactPhoneNumber  = contact.contactPhoneNumber,
            contactEMail        = contact.contactEMail,
            contactGender       = contact.contactGender,
            contactID           = contact.contactID
        )
        return status
    }

    fun getContactList() : ArrayList<Contact>{
        insertFirstContatct()
        val list                            = ArrayList<Contact>()
        val listQuery  = dbContactsQuery.selectAllContacts().executeAsList()

        for (i in 0 until listQuery.size){
            val contactID                = listQuery[i].contactID
            val contactFirstName        = listQuery[i].contactFirstName
            val contactLastName         = listQuery[i].contactLastName
            val contactCountryName      = listQuery[i].countryName
            val contactCountryPrefix    = listQuery[i].countryPrefix
            val contactPhoneNumber      = listQuery[i].contactPhoneNumber
            val contactEMail            = listQuery[i].contactEMail
            val contactGender           = listQuery[i].contactGender
            val contactLocalStorageStats       = true

            val queryRol = Contact(
                contactID,
                contactFirstName,
                contactLastName,
                contactCountryName,
                contactCountryPrefix,
                contactPhoneNumber,
                contactEMail,
                contactGender,
                contactLocalStorageStats
            )
            list += queryRol
        }
        return list
    }

    fun getCountryList(): ArrayList<Country>{
        val countryList = ArrayList<Country>()
        val listQuery = dbContriesQuery.selectAll().executeAsList()

        for(i in 0 until listQuery.size){
            val coutryId         =listQuery[i].countryCodeID
            val countryName     = listQuery[i].countryName
            val countryPrefih   = listQuery[i].countryPrefix
            val queryRol                = Country(coutryId, countryName, countryPrefih)
            countryList += queryRol
        }
        return countryList
    }

    fun insertFirstContatct(){
        dbContactsQuery.insertContacts("Ivan","Ivanov","BULGARIA",
            "ivanivanov@abv.bg","888123123","Male")
        dbContactsQuery.insertContacts("Peter","Petrov","BULGARIA",
            "peterpetrov@abv.bg","888234234","Male")
        dbContactsQuery.insertContacts("Ahmed","Mehmed","TURKEY",
            "ahmedmehmed@abv.bg","888787878","Male")
        dbContactsQuery.insertContacts("Dragan","Draganov","UZBEKISTAN",
            "dragandraganov@abv.bg","888432432","Male")
    }
}
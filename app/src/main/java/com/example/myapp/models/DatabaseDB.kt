package com.example.myapp.models

import android.content.Context
import com.example.myapp.*
import com.example.myapp.ui.activities.ContactActivity.Companion.COUNTRY_NAME_ID
import com.example.myapp.ui.activities.ContactActivity.Companion.FIRST_NAME_ID
import com.example.myapp.ui.activities.ContactActivity.Companion.LAST_NAME_ID
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class DatabaseDB {
    val context                 :Context               = MyApplication.instansce
    val databaseName            :String                = "test.db"
    val driver                  :SqlDriver             = AndroidSqliteDriver(Database.Schema, context, databaseName)
    val database                :Database              = Database(driver)
    val dbContriesQuery         :CountyiesDBQueries    = database.countyiesDBQueries
    val dbContactsQuery         :ContactsDBQueries     = database.contactsDBQueries
    val dbContactEmailQuery     :ContactEmailDBQueries = database.contactEmailDBQueries
    val dbContactPhoneDBQueries :ContactPhoneDBQueries = database.contactPhoneDBQueries



    fun saveNewContact(contact: Contact) : Boolean{
        val status = true
        database.transaction {
            dbContactsQuery.insertContacts(
                contactFirstName    = contact.contactFirstName,
                contactLastName     = contact.contactLastName,
                countryName         = contact.contactCountryName
            )
            for (i in contact.contactPhoneNumber.size-1 downTo 0){
                dbContactPhoneDBQueries.insertContactPhone(
                    phone = contact.contactPhoneNumber.get(i).phone,
                    dataTypeName = contact.contactPhoneNumber.get(i).contactPhoneType
                )
            }
            for (i in contact.contactEMail.size-1 downTo 0){
                dbContactEmailQuery.insertContactEmail(
                    email = contact.contactEMail.get(i).email,
                    dataTypeName = contact.contactEMail.get(i).contactEmailType
                )
            }
        }
        return status
    }

    fun updateContact(list: List<UpdateData>) : Boolean{
        val status = true
        database.transaction {
            for (index in 0 until list.size) {
                if (list[index].typeData == FIRST_NAME_ID) dbContactsQuery.updateFirstName(
                    list[index].value,
                    list[index].id
                ) else if (list[index].typeData == LAST_NAME_ID) dbContactsQuery.updateLastName(
                    list[index].value,
                    list[index].id
                ) else if (list[index].typeData == COUNTRY_NAME_ID) dbContactsQuery.updateCountryName(
                    list[index].value,
                    list[index].id
                )
            }
        }
        return status
    }

    fun getContactList() : ArrayList<Contact>{
        var list = arrayListOf<Contact>()
        var listContactQuery    = listOf<SelectAllContacts>()
        var listContactEmail    = listOf<SelectAllEmails>()
        var listContactPhone    = listOf<SelectAllPhones>()

        database.transaction {
            listContactQuery    = dbContactsQuery.selectAllContacts().executeAsList()
            listContactEmail    = dbContactEmailQuery.selectAllEmails().executeAsList()
            listContactPhone    = dbContactPhoneDBQueries.selectAllPhones().executeAsList()
        }


        for (i in 0 until listContactQuery.size){
            val contactID               = listContactQuery[i].contactID
            val contactFirstName        = listContactQuery[i].contactFirstName
            val contactLastName         = listContactQuery[i].contactLastName
            val contactCountryName      = listContactQuery[i].countryName

            var contactPhoneNumber      = mutableListOf<ContactPhone>()
            for (item in 0 until listContactPhone.size){
                if (listContactPhone[item].contactId == contactID){
                    contactPhoneNumber.add(
                        ContactPhone(
                            contactPhoneId      = listContactPhone[item].contactPhoneId,
                            contactId           = listContactPhone[item].contactId,
                            phone               = listContactPhone[item].phone,
                            contactPhoneType    = listContactPhone[item].dataTypeName
                        )
                    )
                }
            }

            var contactEmail            = mutableListOf<ContactEmail>()
            for (item in 0 until listContactEmail.size){
                if (listContactEmail[item].contactId == contactID){
                    contactEmail.add(
                        ContactEmail(
                            contactEmailId      = listContactEmail[item].contactEmailId,
                            contactId           = listContactEmail[item].contactId,
                            email               = listContactEmail[item].email,
                            contactEmailType    = listContactEmail[item].dataTypeName
                    ))
                }
            }

            val contactLocalStorageStats       = true

            val queryRol = Contact(
                contactID,
                contactFirstName,
                contactLastName,
                contactCountryName,
                contactPhoneNumber,
                contactEmail,
                contactLocalStorageStats,
                contactBlob = null
            )
            list.add(queryRol)
        }
        return list
    }

    fun getCountryList(): ArrayList<Country>{
        val countryList         = ArrayList<Country>()
        val listQuery           = dbContriesQuery.selectAll().executeAsList()

        for(i in 0 until listQuery.size){
            val coutryId        = listQuery[i].countryCodeID
            val countryName     = listQuery[i].countryName
            val countryPrefih   = listQuery[i].countryPrefix
            val queryRol        = Country(coutryId, countryName, countryPrefih)
            countryList += queryRol
        }
        return countryList
    }
}
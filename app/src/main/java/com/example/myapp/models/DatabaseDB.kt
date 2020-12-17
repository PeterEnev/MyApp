package com.example.myapp.models

import android.content.Context
import com.example.myapp.*
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

open class DatabaseDB {
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
                dbContactPhoneDBQueries.insertNewContactPhone(
                    phone = contact.contactPhoneNumber.get(i).phone,
                    dataTypeName = contact.contactPhoneNumber.get(i).contactPhoneType
                )
            }
            for (i in contact.contactEMail.size-1 downTo 0){
                dbContactEmailQuery.insertNewContactEmail(
                    email = contact.contactEMail.get(i).email,
                    dataTypeName = contact.contactEMail.get(i).contactEmailType
                )
            }
        }
        return status
    }

    fun updatePhone(phone: ContactPhone){
        dbContactPhoneDBQueries.updateContactPhone(
            phone.phone,
            phone.contactPhoneType,
            phone.contactPhoneId!!
        )
    }

    fun deletePhone(phone: ContactPhone){
        dbContactPhoneDBQueries.deleteContactPhone(
            phone.contactPhoneId!!
        )
    }

    fun addPhone(phone: ContactPhone){
        dbContactPhoneDBQueries.insertExistingContactPhone(
            phone.contactId!!,
            phone.phone,
            phone.contactPhoneType
        )
    }

    fun updateEmail(email: ContactEmail){
        dbContactEmailQuery.updateContactEmail(
            email.email,
            email.contactEmailType,
            email.contactEmailId!!
        )
    }

    fun deleteEmail(email: ContactEmail){
        dbContactEmailQuery.deleteContactEmail(
            email.contactEmailId!!
        )
    }

    fun addEmail(email: ContactEmail){
        dbContactEmailQuery.insertExistingContactEmail(
            email.contactId!!,
            email.email,
            email.contactEmailType
        )
    }

    fun updateContact(contact: Contact){
        dbContactsQuery.updateContacts(
            contact.contactFirstName,
            contact.contactLastName,
            contact.contactCountryName,
            contact.contactID!!
        )
    }

    fun getContactList(): ArrayList<Contact>{
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
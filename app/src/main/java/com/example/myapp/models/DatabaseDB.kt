package com.example.myapp.models

import android.content.Context
import com.example.myapp.*
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

private const val DATABASE_NAME                                = "test.db"

open class DatabaseDB {
    private val context                 :Context               = MyApplication.instansce
    private val databaseName            :String                = DATABASE_NAME
    private val driver                  :SqlDriver             = AndroidSqliteDriver(Database.Schema, context, databaseName)
            val database                :Database              = Database(driver)
    private val dbContriesQuery         :CountyiesDBQueries    = database.countyiesDBQueries
    private val dbContactsQuery         :ContactsDBQueries     = database.contactsDBQueries
    private val dbContactEmailQuery     :ContactEmailDBQueries = database.contactEmailDBQueries
    private val dbContactPhoneDBQueries :ContactPhoneDBQueries = database.contactPhoneDBQueries


    fun getContactList(): ArrayList<Contact>{

        val list                = arrayListOf<Contact>()
        val listContactQuery    = dbContactsQuery.selectAllContacts().executeAsList()

        for (index in listContactQuery.indices){

            val queryRol = Contact(
                contactID                   = listContactQuery[index].contactID,
                contactFirstName            = listContactQuery[index].contactFirstName,
                contactLastName             = listContactQuery[index].contactLastName,
                contactCountryName          = listContactQuery[index].countryName,
                contactEMail                = null,
                contactPhoneNumber          = null,
                contactLocalStorageStats    = true
            )
            list.add(queryRol)
        }
        return list
    }

    fun getContactEmails(contactId: Long): List<ContactEmail>{
        val list = mutableListOf<ContactEmail>()
        val listEmailsQuery = dbContactEmailQuery.selectContactEmail(contactId).executeAsList()

        for (index in listEmailsQuery.indices){
            list.add (
                ContactEmail(
                contactEmailId      = listEmailsQuery[index].contactEmailId,
                contactId           = listEmailsQuery[index].contactId,
                email               = listEmailsQuery[index].email,
                contactEmailType    = listEmailsQuery[index].dataTypeName
            ))
        }
        return list
    }

    fun getContactPhones(contactId: Long): List<ContactPhone>{
        val list = mutableListOf<ContactPhone>()
        val listPhonesQuery = dbContactPhoneDBQueries.selectContactPhone(contactId).executeAsList()

        for (index in listPhonesQuery.indices){
            list.add( ContactPhone(
                contactPhoneId      = listPhonesQuery[index].contactPhoneId,
                contactId           = listPhonesQuery[index].contactId,
                phone               = listPhonesQuery[index].phone,
                contactPhoneType    = listPhonesQuery[index].dataTypeName
            ))
        }
        return list
    }

    fun saveNewContact(contact: Contact) : Boolean{
        val status = true
        database.transaction {
            dbContactsQuery.insertContacts(
                contactFirstName    = contact.contactFirstName,
                contactLastName     = contact.contactLastName,
                countryName         = contact.contactCountryName!!
            )
            for (index in contact.contactPhoneNumber!!.size-1 downTo 0){
                dbContactPhoneDBQueries.insertNewContactPhone(
                    phone = contact.contactPhoneNumber!!.get(index).phone,
                    dataTypeName = contact.contactPhoneNumber!!.get(index).contactPhoneType
                )
            }
            for (index in contact.contactEMail!!.size-1 downTo 0){
                dbContactEmailQuery.insertNewContactEmail(
                    email = contact.contactEMail!!.get(index).email,
                    dataTypeName = contact.contactEMail!!.get(index).contactEmailType
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
            contact.contactCountryName!!,
            contact.contactID!!
        )
    }

    fun getContactListFull(): ArrayList<Contact>{
        var list                = arrayListOf<Contact>()
        var listContactQuery    = listOf<SelectAllContacts>()
        var listContactEmail    = listOf<SelectAllEmails>()
        var listContactPhone    = listOf<SelectAllPhones>()

        database.transaction {
            listContactQuery    = dbContactsQuery.selectAllContacts().executeAsList()
            listContactEmail    = dbContactEmailQuery.selectAllEmails().executeAsList()
            listContactPhone    = dbContactPhoneDBQueries.selectAllPhones().executeAsList()
        }


        for (index in listContactQuery.indices){
            val contactID               = listContactQuery[index].contactID
            val contactFirstName        = listContactQuery[index].contactFirstName
            val contactLastName         = listContactQuery[index].contactLastName
            val contactCountryName      = listContactQuery[index].countryName

            var contactPhoneNumber      = mutableListOf<ContactPhone>()
            for (item in listContactPhone.indices){
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
            for (item in listContactEmail.indices){
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

            val queryRol = Contact(
                contactID                   = contactID,
                contactFirstName            = contactFirstName,
                contactLastName             = contactLastName,
                contactCountryName          = contactCountryName,
                contactPhoneNumber          = contactPhoneNumber,
                contactEMail                = contactEmail,
                contactLocalStorageStats    = true,
                contactPhoto                = null
            )
            list.add(queryRol)
        }
        return list
    }

    fun getCountryList(): ArrayList<Country>{
        val countryList         = ArrayList<Country>()
        val listQuery           = dbContriesQuery.selectAll().executeAsList()

        for(index in listQuery.indices){
            val countryId        = listQuery[index].countryCodeID
            val countryName     = listQuery[index].countryName
            val countryPrefix   = listQuery[index].countryPrefix
            val queryRol        = Country(countryId, countryName, countryPrefix)
            countryList += queryRol
        }
        return countryList
    }
}
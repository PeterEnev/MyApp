package com.example.myapp.models

import android.content.Context
import android.provider.ContactsContract
import com.example.myapp.MyApplication


private const val STRING_EMPTY                          = ""
private const val STRING_ITEM_PHOTO                     = "item/photo"
private const val STRING_ITEM_PHONE                     = "item/phone"
private const val STRING_ITEM_EMAIL                     = "item/email"
private const val STRING_HOME                           = "Home"
private const val STRING_MOBILE                         = "Mobile"

class PhoneContact {

    private val context: Context = MyApplication.instansce

    fun getContactData(contact: Contact): Contact {

        val url = ContactsContract.Data.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA15,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE

        )
        val selection =
            ContactsContract.Data.CONTACT_ID + " = ? " + "AND " + ContactsContract.Data.MIMETYPE + " in (?, ?, ?)"
        val selectionArgs = arrayOf(
            contact.contactID.toString(),
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
        )
        val cursor = context.contentResolver.query(
            url,
            projection,
            selection,
            selectionArgs,
            null
        )

        val mimetypeIndex       = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)
        val contactIdIndex      = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
        val blobIndex           = cursor.getColumnIndex(ContactsContract.Data.DATA15)
        val nameIndex           = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        val dataIndex           = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)
        val typeIndex           = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE)

        var currentId           = -1L
        val names               = mutableMapOf<Long, String>()
        val phones              = mutableListOf<ContactPhone>()
        val emails              = mutableListOf<ContactEmail>()
        val blobArray           = mutableMapOf<Long, ByteArray?>()

        while (cursor!!.moveToNext()) {

            val id              = cursor.getLong(contactIdIndex)
            val mimetype        = cursor.getString(mimetypeIndex)
            val data            = cursor.getString(dataIndex)
            val type            = cursor.getInt(typeIndex)
            val name            = cursor.getString(nameIndex)
            val blob            = cursor.getBlob(blobIndex)

            if (cursor.isFirst) currentId = id
            if (currentId != id) currentId = id
            if (currentId == id) {
                names[currentId] = name
                if (mimetype.contains(STRING_ITEM_PHONE)) {
                    phones.add(
                        ContactPhone(
                            contactPhoneId      = null,
                            contactId           = currentId,
                            contactPhoneType    = if (type == 1) STRING_HOME else STRING_MOBILE,
                            phone               = data
                        )
                    )
                } else if (mimetype.contains(STRING_ITEM_EMAIL)) {
                    emails.add(
                        ContactEmail(
                            contactEmailId      = null,
                            contactId           = currentId,
                            contactEmailType    = if (type == 1) STRING_HOME else STRING_MOBILE,
                            email               = data
                        )
                    )
                } else if (mimetype.contains(STRING_ITEM_PHOTO)) blobArray[currentId] = blob
            }
        }
        cursor.close()
        for ((id, name) in names) {
            val lastName = STRING_EMPTY
            val country = STRING_EMPTY
            val contactPhoneNumber = mutableListOf<ContactPhone>()
            for (item in 0 until phones.size) {
                if (phones[item].contactId == id) {
                    contactPhoneNumber.add(
                        ContactPhone(
                            contactPhoneId      = null,
                            contactId           = id,
                            phone               = phones[item].phone,
                            contactPhoneType    = phones[item].contactPhoneType
                        )
                    )
                }
            }
            val contactEmail = mutableListOf<ContactEmail>()
            for (item in 0 until emails.size) {
                if (emails[item].contactId == id) {
                    contactEmail.add(
                        ContactEmail(
                            contactEmailId      = null,
                            contactId           = id,
                            email               = emails[item].email,
                            contactEmailType    = emails[item].contactEmailType
                        )
                    )
                }
            }
            var contactPhoto: ByteArray? = null
            for ((index, img) in blobArray) {
                if (index == id) contactPhoto = img
            }
            val contactLocalStorageStats = false
            var contact = Contact(
                contactID                   = id,
                contactFirstName            = name,
                contactLastName             = lastName,
                contactCountryName          = country,
                contactPhoneNumber          = contactPhoneNumber,
                contactEMail                = contactEmail,
                contactLocalStorageStats    = contactLocalStorageStats,
                contactPhoto                = contactPhoto
            )
        }
        return contact
    }
//        while (cursor!!.moveToNext()) {
//
//            val id          = cursor.getLong(contactIdIndex)
//            val mimetype    = cursor.getString(mimetypeIndex)
//            val data        = cursor.getString(dataIndex)
//            val type        = cursor.getInt(typeIndex)
//            val name        = cursor.getString(nameIndex)
//            val blob        = cursor.getBlob(blobIndex)
//
//            println("id $id  >>  mimetype  $mimetype  >> data $data  >> type $type ")
//            contact = Contact(
//                contactID = id,
//                contactPhoneNumber = data,
//                contactEMail =
//            )
//        }
//        cursor.close()
//
//        return contact

    fun getAllNameAndPhoto() : ArrayList<Contact> {
        val allNameAndPhotoList = arrayListOf<Contact>()

        val url = ContactsContract.Data.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA15,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        val selection = ContactsContract.Data.MIMETYPE + " in (?, ?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )
        val cursor = context.contentResolver.query(
            url,
            projection,
            selection,
            selectionArgs,
            ContactsContract.Data.CONTACT_ID
        )
        val contactIdIndex  = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
        val blobIndex       = cursor.getColumnIndex(ContactsContract.Data.DATA15)
        val nameIndex       = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

        var currentId       = -1L
        val names           = mutableMapOf<Long, String>()
        val photoArray      = mutableMapOf<Long, ByteArray?>()

        while (cursor!!.moveToNext()){
            val id          = cursor.getLong(contactIdIndex)
            val name        = cursor.getString(nameIndex)
            val photo       = cursor.getBlob(blobIndex)

            if (cursor.isFirst)   currentId = id
            if (currentId != id)  currentId = id
            if (currentId == id){
                names[currentId]        = name
                photoArray[currentId]   = photo
            }
        }
        cursor.close()

        for ((id, name) in names) {
            var contactPhoto: ByteArray? = null
            for ((index, img) in photoArray) {
                if (index == id) contactPhoto = img
            }
            val queryRol = Contact(
                contactID                   = id,
                contactFirstName            = name,
                contactLastName             = STRING_EMPTY,
                contactCountryName          = null,
                contactPhoneNumber          = null,
                contactEMail                = null,
                contactLocalStorageStats    = false,
                contactPhoto                = contactPhoto
            )
            allNameAndPhotoList.add(queryRol)
        }
        return allNameAndPhotoList
    }

    fun getPhoneContact(): ArrayList<Contact> {

        val contactPhoneList = arrayListOf<Contact>()

        val url = ContactsContract.Data.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA15,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE

        )
        val selection = ContactsContract.Data.MIMETYPE + " in (?, ?, ?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
        )

        val cursor = context.contentResolver.query(
            url,
            projection,
            selection,
            selectionArgs,
            ContactsContract.Data.CONTACT_ID
        )

        val mimetypeIndex   = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)
        val contactIdIndex  = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
        val blobIndex       = cursor.getColumnIndex(ContactsContract.Data.DATA15)
        val nameIndex       = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        val dataIndex       = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)
        val typeIndex       = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE)

        var currentId       = -1L
        val names           = mutableMapOf<Long, String>()
        val phones          = mutableListOf<ContactPhone>()
        val emails          = mutableListOf<ContactEmail>()
        val blobArray       = mutableMapOf<Long, ByteArray?>()

        while (cursor!!.moveToNext()) {

            val id          = cursor.getLong(contactIdIndex)
            val mimetype    = cursor.getString(mimetypeIndex)
            val data        = cursor.getString(dataIndex)
            val type        = cursor.getInt(typeIndex)
            val name        = cursor.getString(nameIndex)
            val blob        = cursor.getBlob(blobIndex)

            if (cursor.isFirst)   currentId     = id
            if (currentId != id)  currentId     = id
            if (currentId == id) {
                names[currentId] = name
                if (mimetype.contains(STRING_ITEM_PHONE)) {
                    phones.add(ContactPhone(
                            contactPhoneId      = null,
                            contactId           = currentId,
                            contactPhoneType    = if (type == 1) STRING_HOME else STRING_MOBILE,
                            phone               = data))
                } else if (mimetype.contains(STRING_ITEM_EMAIL)) {
                    emails.add(ContactEmail(
                            contactEmailId      = null,
                            contactId           = currentId,
                            contactEmailType    = if (type == 1) STRING_HOME else STRING_MOBILE,
                            email               = data))
                } else if (mimetype.contains(STRING_ITEM_PHOTO)) blobArray[currentId] = blob
            }
        }
        cursor.close()
        for ((id, name) in names) {
            val lastName    = STRING_EMPTY
            val country     = STRING_EMPTY
            val contactPhoneNumber = mutableListOf<ContactPhone>()
            for (item in 0 until phones.size) {
                if (phones[item].contactId == id) {
                    contactPhoneNumber.add(
                        ContactPhone(
                            contactPhoneId      = null,
                            contactId           = id,
                            phone               = phones[item].phone,
                            contactPhoneType    = phones[item].contactPhoneType
                        )
                    )
                }
            }
            val contactEmail = mutableListOf<ContactEmail>()
            for (item in 0 until emails.size) {
                if (emails[item].contactId == id) {
                    contactEmail.add(
                        ContactEmail(
                            contactEmailId      = null,
                            contactId           = id,
                            email               = emails[item].email,
                            contactEmailType    = emails[item].contactEmailType
                        )
                    )
                }
            }
            var contactBlob: ByteArray? = null
            for ((index, img) in blobArray) {
                if (index == id) contactBlob = img
            }
            val contactLocalStorageStats = false
            val queryRol = Contact(
                id,
                name,
                lastName,
                country,
                contactPhoneNumber,
                contactEmail,
                contactLocalStorageStats,
                contactBlob
            )
            contactPhoneList.add(queryRol)
        }
        return contactPhoneList
    }
}
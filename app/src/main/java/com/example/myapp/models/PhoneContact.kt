package com.example.myapp.models

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.Settings.Global.getString
import androidx.core.database.getBlobOrNull
import androidx.core.graphics.drawable.toIcon
import com.example.myapp.MyApplication
import com.example.myapp.SelectAllContacts
import com.example.myapp.SelectAllEmails
import com.example.myapp.SelectAllPhones
import com.example.myapp.ui.activities.MainActivity
import java.sql.Blob
import kotlin.random.Random


class PhoneContact {

    val context             : Context = MyApplication.instansce

    fun getPhoneContact(): ArrayList<Contact> {

        var contactPhoneList = arrayListOf<Contact>()

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
        val blobArray       = mutableMapOf<Long, ByteArray>()
//        val typeString      =

        while (cursor!!.moveToNext()) {

            val id          = cursor.getLong(contactIdIndex)
            val mimetype    = cursor.getString(mimetypeIndex)
            val data        = cursor.getString(dataIndex)
            val type        = cursor.getInt(typeIndex)
            val name        = cursor.getString(nameIndex)
            val blob        = cursor.getBlob(blobIndex)

            if (cursor.isFirst) {
                currentId   = id
            }
            if (currentId != id){
                currentId = id
            }

            if (currentId == id) {
                names[currentId] = name
                if (mimetype.contains("item/phone")) {
                    phones.add(
                        ContactPhone(
                            contactPhoneId = null,
                            contactId = currentId.toLong(),
                            contactPhoneType = if (type == 1) "Home" else "Mobile",
                            phone = data
                        )
                    )
                } else if (mimetype.contains("item/email")) {
                    emails.add(
                        ContactEmail(
                            contactEmailId = null,
                            contactId = currentId.toLong(),
                            contactEmailType = if (type == 1) "Home" else "Mobile",
                            email = data
                        )
                    )
                } else if (mimetype.contains("item/photo")) {
                    blobArray[currentId] = blob
                }
            }
        }
        cursor.close()
        for ((id, name) in names) {
            val lastName = MainActivity.EMPTY_STRING
            val country = MainActivity.EMPTY_STRING
            val contactPhoneNumber = mutableListOf<ContactPhone>()
            for (item in 0 until phones.size) {
                if (phones[item].contactId == id) {
                    contactPhoneNumber.add(
                        ContactPhone(
                            contactPhoneId = null,
                            contactId = id,
                            phone = phones[item].phone,
                            contactPhoneType = phones[item].contactPhoneType
                        )
                    )
                }
            }
            val contactEmail = mutableListOf<ContactEmail>()
            for (item in 0 until emails.size) {
                if (emails[item].contactId == id) {
                    contactEmail.add(
                        ContactEmail(
                            contactEmailId = null,
                            contactId = id,
                            email = emails[item].email,
                            contactEmailType = emails[item].contactEmailType
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
                contactBlob = contactBlob
            )
            contactPhoneList.add(queryRol)
        }
        return contactPhoneList
    }
}
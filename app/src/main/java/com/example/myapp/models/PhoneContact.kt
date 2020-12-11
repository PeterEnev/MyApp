package com.example.myapp.models

import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.example.myapp.MyApplication
import com.example.myapp.ui.activities.MainActivity


class PhoneContact {

    val context             : Context = MyApplication.instansce

    fun getPhoneContact(): ArrayList<Contact>{
        var contactPhoneList = ArrayList<Contact>()
        var phoneContact = listOf<Contact>()

        val url = ContactsContract.Data.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA15,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE
        )

        val cursor = context.contentResolver.query(url, projection,null,null,ContactsContract.Data.CONTACT_ID)

        val mimetypeIndex       = cursor?.getColumnIndex(ContactsContract.Data.MIMETYPE)
        val contactIdIndex      = cursor?.getColumnIndex(ContactsContract.Data.CONTACT_ID)
        val blobIndex           = cursor?.getColumnIndex(ContactsContract.Data.DATA15)
        val displayNameIndex    = cursor?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        val dataIndex           = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)
        val typeIndex           = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE)

//        while (cursor!!.moveToNext()){
//            println( " 0 -> " + cursor.getString(0) +
//                    " 1 -> " + cursor.getString(1) +
//                    //" 2 -> " + cursor.getString(2) +
//                    " 3 -> " + cursor.getString(3) +
//                    " 4 -> " + cursor.getString(4) +
//                    " 5 -> " + cursor.getString(5)
//            )
//        }


        val phones = context.contentResolver.query(
            //ContactsContract.Data.CONTENT_URI,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val list = Contact(
                contactID = MainActivity.DEFAULT_VALUE_PHONE_CONTACT,
                contactFirstName = name,
                contactLastName = MainActivity.EMPTY_STRING,
                contactPhoneNumber = phoneNumber,
                contactEMail = MainActivity.EMPTY_STRING,
                contactCountryPrefix = MainActivity.EMPTY_STRING,
                contactCountryName = MainActivity.EMPTY_STRING,
                //contactGender = MainActivity.EMPTY_STRING,
                contactLocalStorageStats = false,
                contactBlob = null
            )
            contactPhoneList.add(list)
        }
        phones.close()
        return contactPhoneList
    }
}
package com.example.myapp.models

import android.content.Context
import android.graphics.Paint
import android.provider.ContactsContract
import com.example.myapp.MyApplication
import com.example.myapp.ui.activities.MainActivity
import kotlin.collections.ArrayList


class PhoneContact {

    val context             : Context = MyApplication.instansce

    fun getPhoneContact(): ArrayList<Contact>{
        var contactPhoneList = ArrayList<Contact>()
//
//        val url = ContactsContract.Contacts.CONTENT_URI
//        val projection = arrayOf(
//            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//            ContactsContract.CommonDataKinds.Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Email.ADDRESS
//        )
//
//        val cursor = context.contentResolver.query(url, projection, null, null, null)
//
//        val name    = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
//        val phone   = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//        val email   = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
//
//        println(cursor.getString(name))




        //val daddada = context.contentResolver.query(ContactsContract.Data.CONTENT_URI)
        val phones = context.contentResolver.query(
            //ContactsContract.Data.CONTENT_URI,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (phones.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val list = Contact(
                contactID                   = MainActivity.DEFAULT_VALUE_PHONE_CONTACT,
                contactFirstName            = name,
                contactLastName             = MainActivity.EMPTY_STRING,
                contactPhoneNumber          = phoneNumber,
                contactEMail                = MainActivity.EMPTY_STRING,
                contactCountryPrefix        = MainActivity.EMPTY_STRING,
                contactCountryName          = MainActivity.EMPTY_STRING,
                contactGender               = MainActivity.EMPTY_STRING,
                contactLocalStorageStats    = false
            )
            contactPhoneList.add(list)
        }
        phones.close()
        return contactPhoneList
    }
}
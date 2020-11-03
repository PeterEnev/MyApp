package com.example.myapp.models

import android.content.Context
import android.provider.ContactsContract
import com.example.myapp.MyApplication
import com.example.myapp.ui.activities.MainActivity


class PhoneContact {

    val context             : Context = MyApplication.instansce
    fun getPhoneContact(): ArrayList<Contact>{
        var contactPhoneList = ArrayList<Contact>()

        val phones = context.contentResolver.query(
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
            println(name)
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
            println(list.toString())
            contactPhoneList.add(list)
        }
        phones.close()
        return contactPhoneList
    }
}
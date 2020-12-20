package com.example.myapp.models

import java.io.Serializable

class Contact(val contactID                   : Long?,
              var contactFirstName            : String,
              var contactLastName             : String,
              var contactCountryName          : String,
              var contactPhoneNumber          : List<ContactPhone>,
              var contactEMail                : List<ContactEmail>,
              var contactLocalStorageStats    : Boolean,
              var contactBlob                 : ByteArray? = null,
              var contactEdit                 : Int = 0 ): Serializable
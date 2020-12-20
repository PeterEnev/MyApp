package com.example.myapp.models

import java.io.Serializable

data class ContactEmail (val contactEmailId          : Long?,
                         var contactId               : Long?,
                         var email                   : String,
                         var contactEmailType        : String,
                         var emailEdit               : Int = 0) : Serializable
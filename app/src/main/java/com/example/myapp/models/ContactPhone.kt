package com.example.myapp.models

import java.io.Serializable

data class ContactPhone(
    val contactPhoneId      : Long?,
    var contactId           : Long?,
    var phone               : String,
    var contactPhoneType    : String,
    var phoneEdit           : Int = 0
) : Serializable
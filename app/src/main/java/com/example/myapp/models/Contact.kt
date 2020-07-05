package com.example.myapp.models

import java.io.Serializable

class Contact(
    val contactID               : Long,
    var contactFirstName        : String,
    var contactLastName         : String,
    var contactCountryName      : String,
    var contactCountryPrefix    : String,
    var contactPhoneNumber      : String,
    var contactEMail            : String,
    var contactGender           : String,
    val contactLocalStorageStats: Boolean
): Serializable
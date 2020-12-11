package com.example.myapp.models

import java.io.Serializable
import java.sql.Blob

class Contact(
    val contactID               : Long,
    var contactFirstName        : String,
    var contactLastName         : String,
    var contactCountryName      : String,
    var contactCountryPrefix    : String,
    var contactPhoneNumber      : String,
    var contactEMail            : String,
    var contactLocalStorageStats: Boolean,
    var contactBlob             : Blob? = null
): Serializable
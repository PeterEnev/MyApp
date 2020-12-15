package com.example.myapp.models

import java.io.Serializable

data class ContactPhone(
    val contactPhoneId          :Long?,
    val contactId               :Long?,
    val phone                   :String,
    val contactPhoneType        :String
) : Serializable
package com.example.myapp.models

data class ContactPhone(
    val contactPhoneId          :Long,
    val contactId               :Long,
    val phone                   :String,
    val contactPhoneTypeId      :Long
)
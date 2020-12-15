package com.example.myapp.models

import java.io.Serializable

data class ContactEmail (
    val contactEmailId          :Long?,
    val contactId               :Long?,
    val email                   :String,
    val contactEmailType        :String
) : Serializable
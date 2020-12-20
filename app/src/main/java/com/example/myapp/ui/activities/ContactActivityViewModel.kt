package com.example.myapp.ui.activities

import androidx.lifecycle.ViewModel
import com.example.myapp.models.Contact
import com.example.myapp.models.ContactEmail
import com.example.myapp.models.ContactPhone


class ContactActivityViewModel: ViewModel() {

    lateinit var editingContact: Contact

    var contactStatusExisting = false
    var phoneLinearLayoutFlag = true
    var emailLinearLayoutFlag = true

}
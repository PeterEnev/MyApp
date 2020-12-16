package com.example.myapp.ui.activities

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModel
import com.example.myapp.ContactPhone
import com.example.myapp.R
import com.example.myapp.models.Contact


class ContactActivityViewModel: ViewModel() {

    lateinit var editingContact          : Contact
    lateinit var parentPhoneLinearLayout : LinearLayout
    lateinit var parentMailLinearLayout  : LinearLayout

    var contactStatusExisting = false

}
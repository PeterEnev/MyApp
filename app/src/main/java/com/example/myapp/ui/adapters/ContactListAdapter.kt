package com.example.myapp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.ui.activities.ContactActivity
import kotlinx.android.synthetic.main.recycler_contact.view.*
import kotlinx.android.synthetic.main.recycler_contact.view.editBtn
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_contact.*
import java.util.*
import kotlin.collections.ArrayList

class ContactListAdapter (val contactList: ArrayList<Contact>) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    companion object{
        val CONTACT_STATUS              = "contactStatus"
        val CONTACT_STATUS_EXISTING     = "existing"
        val CONTACT_NAME                = "contact"
        val TOAST_CONTACT_EDITED        = "The contact cannot be edited"
    }

    class ViewHolder(itemView: View, override val containerView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer{
        fun bindItems(contact: Contact){

            recyclerContactName.text =
                with(contact){contactFirstName + " " + contactLastName}
            recyclerContactPhone.text =
                with(contact){contactCountryPrefix + contactPhoneNumber}
            recyclerContactMail.text =
                with(contact){contactEMail}
            recyclerContactCountry.text =
                with(contact){contactCountryName}
            recyclerContactGender.text =
                with(contact){contactGender}
            if (contact.contactLocalStorageStats){
                imageView.setImageResource(R.drawable.ic_contact_calendar_black)
            }else{
                imageView.setImageResource(R.drawable.phone_android_black)
            }
            expandableLayout.visibility = View.GONE

            itemView.setOnClickListener {
                if (expandableLayout.visibility == GONE){
                    expandableLayout.visibility = View.VISIBLE
                }else{
                    expandableLayout.visibility = View.GONE
                }
            }

            editBtn.setOnClickListener {
                if (contact.contactLocalStorageStats){
                    val intent = Intent(itemView.context, ContactActivity::class.java)
                    intent.putExtra(CONTACT_STATUS, CONTACT_STATUS_EXISTING)
                    intent.putExtra(CONTACT_NAME,
                        Contact(
                            contactID                   = contact.contactID,
                            contactFirstName            = contact.contactFirstName,
                            contactLastName             = contact.contactLastName,
                            contactCountryName          = contact.contactCountryName,
                            contactCountryPrefix        = contact.contactCountryPrefix,
                            contactPhoneNumber          = contact.contactPhoneNumber,
                            contactEMail                = contact.contactEMail,
                            contactGender               = contact.contactGender,
                            contactLocalStorageStats    = contact.contactLocalStorageStats))
                    itemView.context.startActivity(intent)
                }else{
                    Toast.makeText(itemView.context, TOAST_CONTACT_EDITED, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_contact, parent, false)
        return ViewHolder(view, view)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.ViewHolder, position: Int) {
        holder.bindItems(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}
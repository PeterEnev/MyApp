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

class ContactListAdapter (val contactList: ArrayList<Contact>) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    companion object{
        val CONTACT_STATUS              = "contactStatus"
        val CONTACT_STATUS_EXISTING     = "existing"
        val CONTACT_NAME                = "contact"
        val TOAST_CONTACT_EDITED        = "The contact cannot be edited"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(contact: Contact){
            itemView.recyclerContactName.text =
                contact.contactFirstName + " " + contact.contactLastName
            itemView.recyclerContactPhone.text =
                contact.contactCountryPrefix + contact.contactPhoneNumber

            itemView.recyclerContactMail.text =
                contact.contactEMail
            itemView.recyclerContactCountry.text =
                contact.contactCountryName
            itemView.recyclerContactGender.text =
                contact.contactGender
            if (contact.contactLocalStorageStats){
                itemView.imageView.setImageResource(R.drawable.ic_contact_calendar_black)
            }else{
                itemView.imageView.setImageResource(R.drawable.phone_android_black)
            }
            itemView.expandableLayout.visibility = View.GONE
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.ViewHolder, position: Int) {
        holder.bindItems(contactList[position])

        holder.itemView.setOnClickListener {
            if (holder.itemView.expandableLayout.visibility == GONE){
                holder.itemView.expandableLayout.visibility = View.VISIBLE
            }else{
                holder.itemView.expandableLayout.visibility = View.GONE
            }
        }

        holder.itemView.editBtn.setOnClickListener {
                if (contactList[position].contactLocalStorageStats){
                    val intent = Intent(holder.itemView.context, ContactActivity::class.java)
                    intent.putExtra(CONTACT_STATUS, CONTACT_STATUS_EXISTING)
                    intent.putExtra(CONTACT_NAME,
                        Contact(
                            contactID                   = contactList[position].contactID,
                            contactFirstName            = contactList[position].contactFirstName,
                            contactLastName             = contactList[position].contactLastName,
                            contactCountryName          = contactList[position].contactCountryName,
                            contactCountryPrefix        = contactList[position].contactCountryPrefix,
                            contactPhoneNumber          = contactList[position].contactPhoneNumber,
                            contactEMail                = contactList[position].contactEMail,
                            contactGender               = contactList[position].contactGender,
                            contactLocalStorageStats    = contactList[position].contactLocalStorageStats))
                    holder.itemView.context.startActivity(intent)
                }else{
                    Toast.makeText(holder.itemView.context, TOAST_CONTACT_EDITED, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}
package com.example.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.ui.activities.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_contact.*
import kotlin.collections.ArrayList

class ContactListAdapter (private val contactList: ArrayList<Contact>, private val listener: ContactAdapterListener) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    companion object{
        val CONTACT_STATUS              = "contactStatus"
        val CONTACT_STATUS_EXISTING     = "existing"
    }

    inner class ViewHolder(itemView: View, override val containerView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer{

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
                listener.onEditBtnListener(contact, CONTACT_STATUS, CONTACT_STATUS_EXISTING)
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

interface ContactAdapterListener{
    fun onEditBtnListener(contact: Contact, contactStatus: String, contactStatusExisting: String)
}
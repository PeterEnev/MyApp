package com.example.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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
        var CONTACT_STATUS_EXISTING     = true
    }

    inner class ViewHolder(itemView: View, override val containerView: View?) : RecyclerView.ViewHolder(itemView), LayoutContainer{

        fun bindItems(contact: Contact){

            with(contact){
                recyclerContactName.text        = contactFirstName + " " + contactLastName
                recyclerContactPhone.text       = contactCountryPrefix + contactPhoneNumber
                recyclerContactMail.text        = contactEMail
                recyclerContactCountry.text     = contactCountryName
                recyclerContactGender.text      = contactGender
            }

            if (contact.contactLocalStorageStats){
                imageView.setImageResource(R.drawable.ic_contact_calendar_black)
            }else{
                imageView.setImageResource(R.drawable.phone_android_black)
                editBtn.visibility = GONE
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
                CONTACT_STATUS_EXISTING = true
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

class ContactDiffCallback: DiffUtil.ItemCallback<Contact>(){
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.contactID == newItem.contactID
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }

}

interface ContactAdapterListener{
    fun onEditBtnListener(contact: Contact, contactStatus: String, contactStatusExisting: Boolean)
}
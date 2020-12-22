package com.example.myapp.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.models.ContactPhone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_contact.*
import kotlin.collections.ArrayList


private const val STRING_EMPTY                          = ""

class ContactListAdapter (private var contactList: ArrayList<Contact>,
                          private val listener: ContactAdapterListener) :
                                RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    var contactStatusExisting               = true

    fun updateList(newList: ArrayList<Contact>){
        contactList = newList
        notifyDataSetChanged()
    }

    fun updateContactData(contact: Contact, position: Int){
        contactList[position] = contact
        //notifyItemChanged(position)

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

    inner class ViewHolder(itemView: View,
                           override val containerView: View?) :
                                RecyclerView.ViewHolder(itemView), LayoutContainer{

        fun bindItems(contact: Contact){
//                for (row in contact.contactPhoneNumber.indices){
//                    string += "Phone ${contact.contactPhoneNumber[row].contactPhoneType}  ${contact.contactPhoneNumber[row].phone} \n"
//                }
//                recyclerContactPhone.text = string
//
//                string = STRING_EMPTY
//                for (row in contact.contactEMail.indices){
//                    string += "Email ${contact.contactEMail[row].contactEmailType}  ${contact.contactEMail[row].email} \n"
//                }
//                recyclerContactMail.text        = string
//                recyclerContactCountry.text     = contact.contactCountryName

            if (contact.contactLocalStorageStats){
                recyclerContactName.text = contact.contactFirstName + " " + contact.contactLastName
                imageView.setImageResource(R.drawable.ic_contact_calendar_black)
                editBtn.visibility = VISIBLE
            }else{
                recyclerContactName.text        = contact.contactFirstName
                if(contact.contactPhoto != null){
                    val bitmap = BitmapFactory.decodeByteArray(contact.contactPhoto, 0, contact.contactPhoto!!.size)
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.phone_android_black)
                }
                editBtn.visibility = GONE
            }

            expandableLayout.visibility = GONE


            itemView.setOnClickListener {
                var string = STRING_EMPTY
                if (contact.contactPhoneNumber == null && contact.contactEMail == null) {
                    listener.getContactData(contact, adapterPosition)
                }

                for (row in contact.contactPhoneNumber!!.indices){
                    string += "Phone ${contact.contactPhoneNumber!![row].contactPhoneType}  ${contact.contactPhoneNumber!![row].phone} \n"
                }
                recyclerContactPhone.text = string
                string = STRING_EMPTY
                for (row in contact.contactEMail!!.indices){
                    string += "Email ${contact.contactEMail!![row].contactEmailType}  ${contact.contactEMail!![row].email} \n"
                }
                recyclerContactMail.text        = string

                if (expandableLayout.visibility == GONE){
                    expandableLayout.visibility = VISIBLE
                }else{
                    expandableLayout.visibility = GONE
                }
            }
            editBtn.setOnClickListener {
                contactStatusExisting = true
                listener.onEditBtnListener(contact)
            }
        }
    }
}

interface ContactAdapterListener{
    fun onEditBtnListener(contact: Contact)
    fun getContactData(contact: Contact, position: Int)
}
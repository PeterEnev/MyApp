package com.example.myapp.ui.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.helperClasses.Utils
import com.example.myapp.helperClasses.UtilsDefines
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_email.view.*
import kotlinx.android.synthetic.main.list_item_phone.view.*
import kotlinx.android.synthetic.main.recycler_contact.*
import kotlinx.android.synthetic.main.recycler_contact.view.*
import kotlin.collections.ArrayList

class ContactListAdapter (private var contactList: ArrayList<Contact>,
                          private val listener: ContactAdapterListener,
                          private val context: Context,
                          private val itemClick: (Contact) -> Unit) :
                                RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    private lateinit var layout: ConstraintLayout
    private var contactPosition  = -1

    fun updateList(newList: ArrayList<Contact>){
        contactList = newList
        notifyDataSetChanged()
    }

    fun updateContact(contact: Contact){
        contactList[contactPosition] = contact
        expandLayout(layout, contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_contact, parent, false))

    override fun onBindViewHolder(holder: ContactListAdapter.ViewHolder, position: Int) = holder.bindItems(contactList[position])

    override fun getItemCount() = contactList.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{

        fun bindItems(contact: Contact){
            if (contact.contactLocalStorageStats){
                recyclerContactName.text = Utils().fullName(contact.contactFirstName, contact.contactLastName)
                imageView.setImageResource(R.drawable.ic_contact_calendar_black)
                editBtn.visibility = VISIBLE
            }else{
                recyclerContactName.text = Utils().fullName(contact.contactFirstName, null)
                if(contact.contactPhoto != null){
                    val photo = BitmapFactory.decodeByteArray(contact.contactPhoto, UtilsDefines.BITMAP_FACTORY_OFFSET, contact.contactPhoto!!.size)
                    imageView.setImageBitmap(photo)
                } else {
                    imageView.setImageResource(R.drawable.phone_android_black)
                }
                editBtn.visibility = GONE
            }
            expandableLayout.visibility = GONE

            itemView.setOnClickListener {
                contactPosition = adapterPosition
                layout = expandableLayout
                itemClick(contact)
            }
            editBtn.setOnClickListener { listener.onEditBtnListener(contact) }
        }


    }
    private fun expandLayout(expandableLayout: ConstraintLayout, contact: Contact){
        val listPhones  : LinearLayout  = expandableLayout.includePhone
        val listEmails  : LinearLayout  = expandableLayout.includeEmail
        listPhones.removeAllViews()
        listEmails.removeAllViews()
        if (expandableLayout.visibility == GONE){
            showData(listPhones, listEmails,  contact)
            expandableLayout.visibility = VISIBLE
        }else{
            expandableLayout.visibility = GONE
        }
    }

    private fun showData(listPhone: LinearLayout, listEmails: LinearLayout, contact: Contact){
        val inflater = LayoutInflater.from(context)
        for (index in contact.contactPhoneNumber!!.indices){
            val rowView = inflater.inflate(R.layout.list_item_phone, null)
            rowView.addPhoneButton.visibility = GONE
            rowView.removePhoneButton.visibility = GONE
            rowView.phoneInput.isEnabled = false
            rowView.phoneInput.setText(contact.contactPhoneNumber!![index].phone)
            rowView.typePhone.isEnabled = false
            rowView.typePhone.setSelection(Utils().selectedType(contact.contactPhoneNumber!![index].contactPhoneType))
            listPhone.addView(rowView)
        }
        for (index in contact.contactEMail!!.indices){
            val rowView = inflater.inflate(R.layout.list_item_email, null)
            rowView.addEmailButton.visibility = GONE
            rowView.removeEmailButton.visibility = GONE
            rowView.eMailInput.isEnabled = false
            rowView.eMailInput.setText(contact.contactEMail!![index].email)
            rowView.typeEmail.isEnabled = false
            rowView.typeEmail.setSelection(Utils().selectedType(contact.contactEMail!![index].contactEmailType))
            listEmails.addView(rowView)
        }
    }
}

interface ContactAdapterListener{
    fun onEditBtnListener(contact: Contact)
    fun getContactData(contact: Contact):Contact
}
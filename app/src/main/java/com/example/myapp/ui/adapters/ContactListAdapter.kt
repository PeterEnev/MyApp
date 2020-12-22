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
import com.example.myapp.models.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_email.view.*
import kotlinx.android.synthetic.main.list_item_phone.view.*
import kotlinx.android.synthetic.main.recycler_contact.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList


private const val STRING_EMPTY                          = " "

class ContactListAdapter (private var contactList: ArrayList<Contact>,
                          private val listener: ContactAdapterListener,
                          private val context: Context) :
                                RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    fun updateList(newList: ArrayList<Contact>){
        contactList = newList
        notifyDataSetChanged()
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

            if (contact.contactLocalStorageStats){
                recyclerContactName.text = contact.contactFirstName + STRING_EMPTY + contact.contactLastName
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
                if (contact.contactPhoneNumber == null && contact.contactEMail == null) runBlocking {
                    contactList[adapterPosition] = async { listener.getContactData(contact, adapterPosition) }.await()
                    expandLayout(expandableLayout, contactList[adapterPosition])
                } else {
                    expandLayout(expandableLayout, contact)
                }
            }
            editBtn.setOnClickListener {
                listener.onEditBtnListener(contact)
            }
        }

        private fun expandLayout(expandableLayout: ConstraintLayout, contact: Contact){
            val listPhones  : LinearLayout  = includePhone
            val listEmails  : LinearLayout  = includePhone
            if (expandableLayout.visibility == GONE){
                showData(listPhones, listEmails,  contact)
                expandableLayout.visibility = VISIBLE
            }else{
                expandableLayout.visibility = GONE
                listPhones.removeAllViews()
                listEmails.removeAllViews()
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
}

interface ContactAdapterListener{
    fun onEditBtnListener(contact: Contact)
    fun getContactData(contact: Contact, position: Int):Contact
}
package com.example.myapp.ui.adapters

import android.graphics.BitmapFactory
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.Contact
import com.example.myapp.ui.activities.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_contact.*
import kotlin.collections.ArrayList

class ContactListAdapter (private var contactList: ArrayList<Contact>,
                          private val listener: ContactAdapterListener) :
                                RecyclerView.Adapter<ContactListAdapter.ViewHolder>()  {

    companion object{
        var CONTACT_STATUS_EXISTING     = true
    }

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

            with(contact){
                var string = MainActivity.EMPTY_STRING
                for (row in 0 until contactPhoneNumber.size){
                    string += "Phone ${contactPhoneNumber[row].contactPhoneType}  ${contactPhoneNumber[row].phone} \n"
                }
                recyclerContactPhone.text       = string

                string = MainActivity.EMPTY_STRING
                for (row in 0 until contactEMail.size){
                    string += "Email ${contactEMail[row].contactEmailType}  ${contactEMail[row].email} \n"
                }
                recyclerContactMail.text        = string

                recyclerContactCountry.text     = contactCountryName
            }

            if (contact.contactLocalStorageStats){
                recyclerContactName.text = contact.contactFirstName + " " + contact.contactLastName
                imageView.setImageResource(R.drawable.ic_contact_calendar_black)
                editBtn.visibility              = VISIBLE
            }else{
                recyclerContactName.text        = contact.contactFirstName
                if(contact.contactBlob != null){
                    val bitmap = BitmapFactory.decodeByteArray(contact.contactBlob, 0, contact.contactBlob!!.size)
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.phone_android_black)
                }
                editBtn.visibility              = GONE
            }
            expandableLayout.visibility         = GONE

            itemView.setOnClickListener {
                if (expandableLayout.visibility == GONE){
                    expandableLayout.visibility = View.VISIBLE
                }else{
                    expandableLayout.visibility = View.GONE
                }
            }

            editBtn.setOnClickListener {
                CONTACT_STATUS_EXISTING         = true
                listener.onEditBtnListener(contact)
            }
        }
    }
}



interface ContactAdapterListener{
    fun onEditBtnListener(contact: Contact)
}
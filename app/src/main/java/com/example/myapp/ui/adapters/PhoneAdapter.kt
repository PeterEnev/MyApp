package com.example.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.ContactEmail
import com.example.myapp.models.ContactPhone
import kotlinx.android.synthetic.main.list_item_phone.*
import kotlinx.android.extensions.LayoutContainer

class PhoneAdapter (var phoneList: List<ContactPhone>?, private val contactId: Long?):
    RecyclerView.Adapter<PhoneAdapter.ViewHolder>(){

    val phones = mutableListOf<ContactPhone>()
    init {
        val emptyContact = ContactPhone(null, contactId!!, "", "")
        if (phoneList != null) phones.addAll(phoneList!!) else phones.add(emptyContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_phone, parent, false)
        return ViewHolder(view, view)
    }
    override fun getItemCount(): Int {
        return phones.size
    }

    override fun onBindViewHolder(holder: PhoneAdapter.ViewHolder, position: Int) {
        holder.bindItems(phones[position])
    }

    inner class ViewHolder(itemView: View, override val containerView: View?)
        :RecyclerView.ViewHolder(itemView), LayoutContainer {

        fun bindItems(phone: ContactPhone) {
            phoneInput.setText(phone.phone)
            typePhone.setSelection(selectedType(phone.contactPhoneType))

            if (adapterPosition == 0){
                addPhoneButton.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_add))
                addPhoneButton.setOnClickListener { addNewPhone() }
            }else{
                addPhoneButton.setImageDrawable(getDrawable(itemView.context, R.drawable.remove_black_24dp))
                addPhoneButton.setOnClickListener { removePhone(adapterPosition) }
            }
        }
    }
    fun addNewPhone(){
        phones.add(ContactPhone(null, contactId, "", ""))
        notifyItemInserted(phones.size-1)
    }

    fun removePhone(position: Int){
        phones.removeAt(position)
        notifyItemRemoved(position)
    }

    fun selectedType(type: String) : Int{
        return when (type){
            "Home"      -> 0
            "Mobile"    -> 1
            "Work"      -> 2
            else        -> 3
        }
    }
}
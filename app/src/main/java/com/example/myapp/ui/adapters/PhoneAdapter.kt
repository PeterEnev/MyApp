package com.example.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.ContactPhone
import kotlinx.android.synthetic.main.list_item_phone.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_email.*

private const val EMPTY_STRING              = ""
private const val SPINNER_HOME              = "Home"
private const val SPINNER_MOBILE            = "Mobile"
private const val SPINNER_WORK              = "Work"
private const val DATA_CREATE               = 3

class PhoneAdapter (phoneList: List<ContactPhone>,
                    private val listener: PhoneAdapterListener):
    RecyclerView.Adapter<PhoneAdapter.ViewHolder>(){

    private val phones                      = mutableListOf<ContactPhone>()
    private val emptyContactPhone           = ContactPhone( null,
                                                            null,
                                                            EMPTY_STRING,
                                                            EMPTY_STRING,
                                                            DATA_CREATE)

    init {
        phones.addAll(phoneList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_phone, parent, false)
        return ViewHolder(view, view)
    }
    override fun getItemCount() = phones.size

    override fun onBindViewHolder(holder: PhoneAdapter.ViewHolder, position: Int) {
        holder.bindItems(phones[position])
    }

    inner class ViewHolder(itemView: View,
                           override val containerView: View?)
        :RecyclerView.ViewHolder(itemView), LayoutContainer {

        fun bindItems(phone: ContactPhone) {
            phoneInput.setText(phone.phone)
            typePhone.setSelection(selectedType(phone.contactPhoneType))

            if (adapterPosition == phones.size-1){
                addPhoneButton.visibility = View.VISIBLE
                addPhoneButton.setOnClickListener { addNewPhone(adapterPosition) }
                removePhoneButton.setOnClickListener { removePhone(adapterPosition) }
            }else{
                addPhoneButton.visibility = View.GONE
                removePhoneButton.setOnClickListener { removePhone(adapterPosition) }
            }

            phoneInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && phoneInput.text!!.count() !in 8..12){
                    phoneTxt.setErrorEnabled(true)
                    phoneTxt.error = "Please enter a valid Phone Number"
                } else {
                    phoneTxt.setErrorEnabled(false)
                }
            }

            phoneInput.doOnTextChanged { text, start, count, after ->
                listener.notifyDataChangedPhoneRow(adapterPosition, text.toString(),typePhone.selectedItem.toString())
                phones[adapterPosition].phone = text.toString()
                phones[adapterPosition].contactPhoneType = typePhone.selectedItem.toString()
            }
        }
    }
    private fun addNewPhone(id: Int){
        phones.add(emptyContactPhone)
        listener.addNewPhoneRow(id)
        notifyDataSetChanged()
        //notifyItemInserted(phones.size-1)
    }

    private fun removePhone(position: Int){
        listener.deletePhoneRow(position)
        phones.removeAt(position)
        if (phones.size == 0){
            phones.add(emptyContactPhone)
            listener.addNewPhoneRow(0)
        }
        notifyDataSetChanged()
        //notifyItemRemoved(position)
    }

    private fun selectedType(type: String) : Int{
        return when (type){
            SPINNER_HOME      -> 0
            SPINNER_MOBILE    -> 1
            SPINNER_WORK      -> 2
            else              -> 3
        }
    }
}

interface PhoneAdapterListener{
    fun addNewPhoneRow(id: Int)
    fun deletePhoneRow(id: Int)
    fun notifyDataChangedPhoneRow(position: Int, phone: String, type: String)
}
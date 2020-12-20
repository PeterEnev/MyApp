package com.example.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.ContactEmail
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_email.*

private const val EMPTY_STRING = ""

class EmailAdapter(emailList: List<ContactEmail>,
                   private val listener: EmailAdapterListener):
    RecyclerView.Adapter<EmailAdapter.ViewHolder>(){

    private val emails                      = mutableListOf<ContactEmail>()
    private val emptyContactEmail           = ContactEmail(null,
                                                            null,
                                                            EMPTY_STRING,
                                                            EMPTY_STRING,
                                                            3)

    init {
        emails.addAll(emailList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_email, parent, false)
        return ViewHolder(view, view)
    }

    override fun getItemCount() = emails.size

    override fun onBindViewHolder(holder: EmailAdapter.ViewHolder, position: Int) {
        holder.bindItem(emails[position])
    }

    inner class ViewHolder(itemView: View,
                           override val containerView: View?):
        RecyclerView.ViewHolder(itemView), LayoutContainer{

        fun bindItem(email: ContactEmail){
            eMailInput.setText(email.email)
            typeEmail.setSelection(selectedType(email.contactEmailType))

            if (adapterPosition == 0){
                addEmailButton.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_add))
                addEmailButton.setOnClickListener { addNewEmail(adapterPosition) }
            }else{
                addEmailButton.setImageDrawable(getDrawable(itemView.context, R.drawable.remove_black_24dp))
                addEmailButton.setOnClickListener { removeEmail(adapterPosition) }
            }

            eMailInput.doOnTextChanged { text, start, count, after ->
                listener.notifyDataChangedEmailRow(adapterPosition, text.toString(), typeEmail.selectedItem.toString())
            }
        }
    }

    private fun addNewEmail(id: Int){
        emails.add(emptyContactEmail)
        listener.addNewEmailRow(id)
        notifyItemInserted(emails.size-1)
    }

    private fun removeEmail(position: Int){
        listener.deleteEmailRow(position)
        emails.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun selectedType(type: String) : Int{
        return when (type){
            "Home"      -> 0
            "Mobile"    -> 1
            "Work"      -> 2
            else        -> 3
        }
    }
}

interface EmailAdapterListener{
    fun addNewEmailRow(id: Int)
    fun deleteEmailRow(id: Int)
    fun notifyDataChangedEmailRow(position: Int, email: String, type: String)
}
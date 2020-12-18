package com.example.myapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.ContactEmail
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_email.*

class EmailAdapter(var emailList: List<ContactEmail>?, private val contactId: Long?):
    RecyclerView.Adapter<EmailAdapter.ViewHolder>(){

    val emails = mutableListOf<ContactEmail>()

    init {
        val emptyContact = ContactEmail(null, contactId!!, "", "")
        if (emailList != null) emails.addAll(emailList!!) else emails.add(emptyContact)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_email, parent, false)
        return ViewHolder(view, view)
    }

    override fun getItemCount(): Int {
        return emails.size
    }

    override fun onBindViewHolder(holder: EmailAdapter.ViewHolder, position: Int) {
        holder.bindItem(emails[position])
    }

    inner class ViewHolder(itemView: View, override val containerView: View?): RecyclerView.ViewHolder(itemView), LayoutContainer{
        fun bindItem(email: ContactEmail){
            eMailInput.setText(email.email)
            typeEmail.setSelection(selectedType(email.contactEmailType))

            if (adapterPosition == 0){
                addEmailButton.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_add))
                addEmailButton.setOnClickListener { addNewEmail() }
            }else{
                addEmailButton.setImageDrawable(getDrawable(itemView.context, R.drawable.remove_black_24dp))
                addEmailButton.setOnClickListener { removeEmail(adapterPosition) }
            }
        }
    }

    fun addNewEmail(){
        emails.add(ContactEmail(null, contactId!!, "", ""))
        notifyItemInserted(emails.size-1)
    }

    fun removeEmail(position: Int){
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
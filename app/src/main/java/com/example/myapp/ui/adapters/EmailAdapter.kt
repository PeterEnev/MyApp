package com.example.myapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.models.ContactEmail
import com.example.myapp.helperClasses.Utils
import com.example.myapp.helperClasses.UtilsDefines
import com.example.myapp.helperClasses.Validator
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_email.*

class EmailAdapter(emailList: List<ContactEmail>,
                   private val context: Context,
                   private val listener: EmailAdapterListener):
    RecyclerView.Adapter<EmailAdapter.ViewHolder>(){

    private val emails                      = mutableListOf<ContactEmail>()
    private val emptyContactEmail           = ContactEmail(null,
                                                            null,
                                                            UtilsDefines.EMPTY_STRING,
                                                            UtilsDefines.EMPTY_STRING,
                                                            UtilsDefines.CODE_DATA_CREATE)

    init {
        emails.addAll(emailList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_email, parent, false))

    override fun getItemCount() = emails.size

    override fun onBindViewHolder(holder: EmailAdapter.ViewHolder, position: Int) = holder.bindItem(emails[position])

    inner class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer{

        fun bindItem(email: ContactEmail){
            eMailInput.setText(email.email)
            typeEmail.setSelection(Utils().selectedType(email.contactEmailType))

            if (adapterPosition == emails.size-1){
                addEmailButton.visibility = View.VISIBLE
                addEmailButton.setOnClickListener { addNewEmail(adapterPosition) }
                removeEmailButton.setOnClickListener { removeEmail(adapterPosition) }
            }else{
                addEmailButton.visibility = View.GONE
                removeEmailButton.setOnClickListener { removeEmail(adapterPosition) }
            }
            eMailInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && Validator().isNameValid(eMailInput.text.toString())){
                    emailTxt.setErrorEnabled(true)
                    emailTxt.error = context.getString(R.string.MSG_ENTER_VALID_EMAIL_ADDRESS)
                } else {
                    emailTxt.setErrorEnabled(false)
                }
            }

            eMailInput.doOnTextChanged { text, start, count, after ->
                listener.notifyDataChangedEmailRow(adapterPosition, text.toString(), typeEmail.selectedItem.toString())
                emails[adapterPosition].email = text.toString()
                emails[adapterPosition].contactEmailType = typeEmail.selectedItem.toString()
            }
        }
    }

    private fun addNewEmail(id: Int){
        emails.add(emptyContactEmail)
        listener.addNewEmailRow(id)
        notifyDataSetChanged()
    }

    private fun removeEmail(position: Int){
        listener.deleteEmailRow(position)
        emails.removeAt(position)
        if (emails.size == UtilsDefines.BASE_EMAIL_SIZE){
            emails.add(emptyContactEmail)
            listener.addNewEmailRow(UtilsDefines.BASE_EMAIL_SIZE)
        }
        notifyDataSetChanged()
    }
}

interface EmailAdapterListener{
    fun addNewEmailRow(id: Int)
    fun deleteEmailRow(id: Int)
    fun notifyDataChangedEmailRow(position: Int, email: String, type: String)
}
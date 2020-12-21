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

private const val EMPTY_STRING                      = ""
private const val SPINNER_HOME                      = "Home"
private const val SPINNER_MOBILE                    = "Mobile"
private const val SPINNER_WORK                      = "Work"
private const val DATA_CREATE                       = 3
private       val EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"

class EmailAdapter(emailList: List<ContactEmail>,
                   private val listener: EmailAdapterListener):
    RecyclerView.Adapter<EmailAdapter.ViewHolder>(){

    private val emails                      = mutableListOf<ContactEmail>()
    private val emptyContactEmail           = ContactEmail(null,
                                                            null,
                                                            EMPTY_STRING,
                                                            EMPTY_STRING,
                                                            DATA_CREATE)

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

            if (adapterPosition == emails.size-1){
                addEmailButton.visibility = View.VISIBLE
                addEmailButton.setOnClickListener { addNewEmail(adapterPosition) }
                removeEmailButton.setOnClickListener { removeEmail(adapterPosition) }
            }else{
                addEmailButton.visibility = View.GONE
                removeEmailButton.setOnClickListener { removeEmail(adapterPosition) }
            }
            eMailInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !EMAIL_REGEX.toRegex().matches(eMailInput.text!!)){
                    emailTxt.setErrorEnabled(true)
                    emailTxt.error = "Please enter a valid Email Address"
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
        //notifyItemInserted(emails.size-1)
    }

    private fun removeEmail(position: Int){
        listener.deleteEmailRow(position)
        emails.removeAt(position)
        if (emails.size == 0){
            emails.add(emptyContactEmail)
            listener.addNewEmailRow(0)
        }
        notifyDataSetChanged()
        //notifyItemRemoved(position)
    }

    private fun selectedType(type: String) : Int{
        return when (type){
            SPINNER_HOME     -> 0
            SPINNER_MOBILE   -> 1
            SPINNER_WORK     -> 2
            else             -> 3
        }
    }
}

interface EmailAdapterListener{
    fun addNewEmailRow(id: Int)
    fun deleteEmailRow(id: Int)
    fun notifyDataChangedEmailRow(position: Int, email: String, type: String)
}
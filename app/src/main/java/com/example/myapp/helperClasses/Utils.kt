package com.example.myapp.helperClasses

import android.content.Context
import android.content.res.Resources
import com.example.myapp.MyApplication
import com.example.myapp.R

class Utils {

    private val context                 : Context = MyApplication.instansce
    companion object{
        private const val CODE_MSG_SAVE_SUCCESSFUL                      = 0
        private const val CODE_MSG_ENTER_VALID_FIRST_NAME               = 1
        private const val CODE_MSG_ENTER_VALID_LAST_NAME                = 2
        private const val CODE_MSG_ENTER_VALID_EMAIL_ADDRESS            = 3
        private const val CODE_MSG_ENTER_VALID_PHONE_NUMBER             = 4
        private const val CODE_MSG_ENTER_COUNTRY                        = 5
        private const val CODE_MSG_TIMEOUT                              = 6
        private const val SPINNER_HOME                                  = "Home"
        private const val SPINNER_MOBILE                                = "Mobile"
        private const val SPINNER_WORK                                  = "Work"
        private const val STRING_EMPTY                                  = " "

    }
    fun getToastMsg(codeMsg: Int): String{
        return when (codeMsg){
            CODE_MSG_SAVE_SUCCESSFUL             -> context.getString(R.string.MSG_SAVE_SUCCESSFUL)
            CODE_MSG_ENTER_VALID_FIRST_NAME      -> context.getString(R.string.MSG_ENTER_VALID_FIRST_NAME)
            CODE_MSG_ENTER_VALID_LAST_NAME       -> context.getString(R.string.MSG_ENTER_VALID_LAST_NAME)
            CODE_MSG_ENTER_VALID_EMAIL_ADDRESS   -> context.getString(R.string.MSG_ENTER_VALID_EMAIL_ADDRESS)
            CODE_MSG_ENTER_VALID_PHONE_NUMBER    -> context.getString(R.string.MSG_ENTER_VALID_PHONE_NUMBER)
            CODE_MSG_ENTER_COUNTRY               -> context.getString(R.string.MSG_ENTER_COUNTRY)
            CODE_MSG_TIMEOUT                     -> context.getString(R.string.MSG_TIMEOUT)
            else                                 -> context.getString(R.string.MSG_OOPS)
        }
    }

    fun selectedType(type: String) : Int{
        return when (type){
            SPINNER_HOME      -> 0
            SPINNER_MOBILE    -> 1
            SPINNER_WORK      -> 2
            else              -> 3
        }
    }

    fun fullName(firstName: String, lastName: String?): String{
        return if (lastName != null)  firstName + STRING_EMPTY + lastName else firstName
    }
}
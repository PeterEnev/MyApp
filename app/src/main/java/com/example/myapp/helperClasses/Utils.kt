package com.example.myapp.helperClasses

import android.content.Context
import com.example.myapp.MyApplication
import com.example.myapp.R

class Utils {

    private val context                 : Context = MyApplication.instansce

    fun getToastMsg(codeMsg: Int): String{
        return when (codeMsg){
            UtilsDefines.CODE_MSG_DATA_VALID                -> context.getString(R.string.MSG_SAVE_SUCCESSFUL)
            UtilsDefines.CODE_MSG_ENTER_VALID_FIRST_NAME    -> context.getString(R.string.MSG_ENTER_VALID_FIRST_NAME)
            UtilsDefines.CODE_MSG_ENTER_VALID_LAST_NAME     -> context.getString(R.string.MSG_ENTER_VALID_LAST_NAME)
            UtilsDefines.CODE_MSG_ENTER_VALID_EMAIL_ADDRESS -> context.getString(R.string.MSG_ENTER_VALID_EMAIL_ADDRESS)
            UtilsDefines.CODE_MSG_ENTER_VALID_PHONE_NUMBER  -> context.getString(R.string.MSG_ENTER_VALID_PHONE_NUMBER)
            UtilsDefines.CODE_MSG_ENTER_COUNTRY             -> context.getString(R.string.MSG_ENTER_COUNTRY)
            UtilsDefines.CODE_MSG_TIMEOUT                   -> context.getString(R.string.MSG_TIMEOUT)
            else                                            -> context.getString(R.string.MSG_OOPS)
        }
    }

    fun selectedType(type: String) : Int{
        return when (type){
            UtilsDefines.SPINNER_STRING_HOME      -> UtilsDefines.SPINNER_INDEX_HOME
            UtilsDefines.SPINNER_STRING_MOBILE    -> UtilsDefines.SPINNER_INDEX_MOBILE
            UtilsDefines.SPINNER_STRING_WORK      -> UtilsDefines.SPINNER_INDEX_WORK
            else                                  -> UtilsDefines.SPINNER_INDEX_OTHER
        }
    }

    fun fullName(firstName: String, lastName: String?): String{
        return if (lastName != null)  firstName + UtilsDefines.EMPTY_STRING + lastName else firstName
    }
}
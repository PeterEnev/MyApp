package com.example.myapp.helperClasses

import android.content.res.Resources
import com.example.myapp.R

class Utils {

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

    }
    fun getToastMsg(codeMsg: Int): String{
        return when (codeMsg){
            CODE_MSG_SAVE_SUCCESSFUL             -> Resources.getSystem().getString(R.string.MSG_SAVE_SUCCESSFUL)
            CODE_MSG_ENTER_VALID_FIRST_NAME      -> Resources.getSystem().getString(R.string.MSG_ENTER_VALID_FIRST_NAME)
            CODE_MSG_ENTER_VALID_LAST_NAME       -> Resources.getSystem().getString(R.string.MSG_ENTER_VALID_LAST_NAME)
            CODE_MSG_ENTER_VALID_EMAIL_ADDRESS   -> Resources.getSystem().getString(R.string.MSG_ENTER_VALID_EMAIL_ADDRESS)
            CODE_MSG_ENTER_VALID_PHONE_NUMBER    -> Resources.getSystem().getString(R.string.MSG_ENTER_VALID_PHONE_NUMBER)
            CODE_MSG_ENTER_COUNTRY               -> Resources.getSystem().getString(R.string.MSG_ENTER_COUNTRY)
            CODE_MSG_TIMEOUT                     -> Resources.getSystem().getString(R.string.MSG_TIMEOUT)
            else                                 -> Resources.getSystem().getString(R.string.MSG_OOPS)
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
}
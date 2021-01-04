package com.example.myapp.helperClasses

import com.example.myapp.models.Contact

class Validator {
    fun checkContact(contact: Contact): Int{
        var result = UtilsDefines.CODE_MSG_DATA_VALID
        if (!isNameValid(contact.contactFirstName)){
            result = UtilsDefines.CODE_MSG_ENTER_VALID_FIRST_NAME
        }else if (!isNameValid(contact.contactLastName)){
            result = UtilsDefines.CODE_MSG_ENTER_VALID_LAST_NAME
        }else if (!isCountryValid(contact.contactCountryName!!)){
            result = UtilsDefines.CODE_MSG_ENTER_COUNTRY
        } else{
            for (index in contact.contactEMail!!.indices){
                if(!isEmailValid(contact.contactEMail!![index].email)) {
                    result = UtilsDefines.CODE_MSG_ENTER_VALID_EMAIL_ADDRESS
                }
            }
            for (index in contact.contactPhoneNumber!!.indices){
                if (!isPhoneValid(contact.contactPhoneNumber!![index].phone)){
                    result = UtilsDefines.CODE_MSG_ENTER_VALID_PHONE_NUMBER
                }
            }
        }
        return result
    }

    fun isNameValid(name: String) = UtilsDefines.NAME_REGEX.toRegex().matches(name)

    fun isPhoneValid(phone: String) = UtilsDefines.PHONE_REGEX.toRegex().matches(phone)

    private fun isCountryValid(string: String) = UtilsDefines.STRING_REGEX.toRegex().matches(string)

    private fun isEmailValid(email: String) = UtilsDefines.EMAIL_REGEX.toRegex().matches(email)
}
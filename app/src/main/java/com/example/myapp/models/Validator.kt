package com.example.myapp.models

class Validator {
    companion object {

        const val EMAIL_REGEX                               = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                                                              "\\@" +
                                                              "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                                                              "(" +
                                                              "\\." +
                                                              "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                                                              ")+"
        const val NAME_REGEX                                 = "^[A-Za-z]{2,16}"
        const val PHONE_REGEX                                = "^[+0-9]{9,15}"
        const val STRING_REGEX                               = "^[A-Za-z]{2,20}"
        const val CODE_MSG_DATA_VALID                        = 0
        const val CODE_MSG_ENTER_VALID_FIRST_NAME            = 1
        const val CODE_MSG_ENTER_VALID_LAST_NAME             = 2
        const val CODE_MSG_ENTER_VALID_EMAIL_ADDRESS         = 3
        const val CODE_MSG_ENTER_VALID_PHONE_NUMBER          = 4
        const val CODE_MSG_ENTER_COUNTRY                     = 5

    }
    fun checkContact(contact: Contact): Int{
        var result = CODE_MSG_DATA_VALID
        if (!isNameValid(contact.contactFirstName)){
            result = CODE_MSG_ENTER_VALID_FIRST_NAME
        }else if (!isNameValid(contact.contactLastName)){
            result = CODE_MSG_ENTER_VALID_LAST_NAME
        }else if (!isCountryValid(contact.contactCountryName!!)){
            result = CODE_MSG_ENTER_COUNTRY
        } else{
            for (index in contact.contactEMail!!.indices){
                if(!isEmailValid(contact.contactEMail!![index].email)) {
                    result = CODE_MSG_ENTER_VALID_EMAIL_ADDRESS
                }
            }
            for (index in contact.contactPhoneNumber!!.indices){
                if (!isPhoneValid(contact.contactPhoneNumber!![index].phone)){
                    result = CODE_MSG_ENTER_VALID_PHONE_NUMBER
                }
            }
        }
        return result
    }

    private fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email);
    }

    fun isNameValid(name: String): Boolean {
        return NAME_REGEX.toRegex().matches(name)
    }

    fun isPhoneValid(phone: String): Boolean {
        return PHONE_REGEX.toRegex().matches(phone)
    }

    private fun isCountryValid(string: String): Boolean {
        return STRING_REGEX.toRegex().matches(string)
    }
}
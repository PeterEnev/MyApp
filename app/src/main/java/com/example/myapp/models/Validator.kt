package com.example.myapp.models

class Validator {
    companion object {
        @JvmStatic
        //val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        val EMAIL_REGEX = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        val NAME_REGEX                                  = "^[A-Za-z]{3,16}"
        val PHONE_REGEX                                 = "^[0-9]{9,10}"
        val STRING_REGEX                                = "^[A-Za-z]{3,16}"
        val CHECK_STRING_VALID              : String    = "valid" // 0
        val MSG_ENTER_VALID_FIRST_NAME      : String    = "Please enter a valid First Name" // 1
        val MSG_ENTER_VALID_LAST_NAME       : String    = "Please enter a valid Last Name"  // 2
        val MSG_ENTER_VALID_EMAIL_ADDRESS   : String    = "Please enter a valid Email Address" // 3
        val MSG_ENTER_VALID_PHONE_NUMBER    : String    = "Please enter a valid Phone Number" // 4
        val MSG_ENTER_VALID_GENDER          : String    = "Please enter a Gender"             // 5

    }
    fun checkContact(contact: Contact): Int{
        var result = 0
        if (!isNameValid(contact.contactFirstName)){
            result = 1
            return result
        }else if (!isNameValid(contact.contactLastName)){
            result = 2
            return result
//        }else if (!isEmailValid(contact.contactEMail)){
//            result = 3
//            return result
//        }else if (!isPhoneValid(contact.contactPhoneNumber)){
//            result = 4
//            return result
//        }else if (!isStringValid(contact.contactGender)){
//            result = 5
//            return result
        }else{
            return result
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email);
    }

    private fun isNameValid(name: String): Boolean {
        return NAME_REGEX.toRegex().matches(name)
    }

    private fun isPhoneValid(phone: String): Boolean {
        return PHONE_REGEX.toRegex().matches(phone)
    }

    private fun isStringValid(string: String): Boolean {
        return STRING_REGEX.toRegex().matches(string)
    }
}
package com.example.myapp.helperClasses

class UtilsDefines {
    companion object{
        const val CONTACT_SERIALIZABLE_EXTRA            = "data"
        const val CONTACT_EXISTING_BOOLEAN_EXTRA        = "existing"

        const val SPINNER_STRING_HOME                   = "Home"
        const val SPINNER_STRING_MOBILE                 = "Mobile"
        const val SPINNER_STRING_WORK                   = "Work"
        const val SPINNER_INDEX_HOME                    = 0
        const val SPINNER_INDEX_MOBILE                  = 1
        const val SPINNER_INDEX_WORK                    = 2
        const val SPINNER_INDEX_OTHER                   = 3

        const val EMPTY_STRING                          = " "
        const val DEFAULT_VALUE_NEW_CONTACT             = -1L
        const val DEFAULT_VALUE_ADAPTER_POSITION        = -1
        const val BITMAP_FACTORY_OFFSET                 = 0
        const val TIME_OUT_IN_MILLISECONDS              = 3000L
        const val STORAGE_PERMISSION_CODE               = 1
        const val REQUEST_CODE_OK                       = 0
        const val BASE_EMAIL_SIZE                       = 0
        const val BASE_PHONE_SIZE                       = 0

        const val CODE_DATA_VALID                       = 0

        const val CODE_DATA_EXISTS                      = 0
        const val CODE_DATA_UPDATE                      = 1
        const val CODE_DATA_DELETE                      = 2
        const val CODE_DATA_CREATE                      = 3

        const val CODE_MSG_DATA_VALID                   = 0
        const val CODE_MSG_ENTER_VALID_FIRST_NAME       = 1
        const val CODE_MSG_ENTER_VALID_LAST_NAME        = 2
        const val CODE_MSG_ENTER_VALID_EMAIL_ADDRESS    = 3
        const val CODE_MSG_ENTER_VALID_PHONE_NUMBER     = 4
        const val CODE_MSG_ENTER_COUNTRY                = 5
        const val CODE_MSG_TIMEOUT                      = 6
        const val CODE_MSG_OPS                          = 7

        const val EMAIL_REGEX                           = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                                                          "\\@" +
                                                          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                                                          "(" +
                                                          "\\." +
                                                          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                                                          ")+"
        const val NAME_REGEX                            = "^[A-Za-z]{2,16}"
        const val PHONE_REGEX                           = "^[+0-9]{9,15}"
        const val STRING_REGEX                          = "^[A-Za-z]{2,20}"
    }
}
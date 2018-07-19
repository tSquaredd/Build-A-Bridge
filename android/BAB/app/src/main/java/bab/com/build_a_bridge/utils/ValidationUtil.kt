package bab.com.build_a_bridge.utils

class ValidationUtil {
    companion object {
        /**
         * If the input name is valid returns null, otherwise returns the first
         * invalid character it finds
         */
        fun isNameValid(name: String): Char? {
            for(char in name){
                if(char !in 'a'..'z' && char !in 'A'..'Z')
                    return char
            }
            return null
        }


        /**
         *  basic phone number validator. Only accepts numbers without special characters
         *  such as '-'. //TODO: IS THIS OKAY?
         */
        fun isNumberValid(number: String): Boolean {
            for( char in number)
                if(char !in '0'..'9')
                    return false
            return true
        }
    }
}
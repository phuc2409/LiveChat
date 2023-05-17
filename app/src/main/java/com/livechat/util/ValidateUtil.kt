package com.livechat.util

import android.util.Patterns

/**
 * User: Quang Phúc
 * Date: 2023-05-16
 * Time: 12:43 AM
 */
object ValidateUtil {

    fun isValidEmail(email: String): Boolean {
        return if (email.isBlank()) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }
        if (password.firstOrNull { it.isDigit() } == null) {
            return false
        }
        if (password.firstOrNull { it.isLetter() } == null) {
            return false
        }
//        if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) {
//            return false
//        }
//        if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) {
//            return false
//        }
//        if (password.firstOrNull { !it.isLetterOrDigit() } == null) {
//            return false
//        }

        return true
    }
}

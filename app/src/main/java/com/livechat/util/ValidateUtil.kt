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
}

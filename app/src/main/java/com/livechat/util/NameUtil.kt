package com.livechat.util

/**
 * User: Quang Phúc
 * Date: 2023-06-17
 * Time: 05:13 PM
 */
object NameUtil {

    private val convert = hashMapOf(
        Pair('0', '0'),
        Pair('1', 'a'),
        Pair('2', 'b'),
        Pair('3', 'c'),
        Pair('4', 'd'),
        Pair('5', 'e'),
        Pair('6', 'f'),
        Pair('7', 'g'),
        Pair('8', 'h'),
        Pair('9', 'i')
    )

    fun generateUserNameFromEmail(email: String): String {
        val emailHashCode = email.hashCode()
        val userNames = if (emailHashCode < 0) {
            "0${-emailHashCode}"
        } else {
            emailHashCode.toString()
        }.toCharArray()
        var userName = ""

        for (i in userNames) {
            userName += if ((0..1).random() == 0) {
                convert[i]
            } else {
                i
            }
        }
        return userName
    }
}
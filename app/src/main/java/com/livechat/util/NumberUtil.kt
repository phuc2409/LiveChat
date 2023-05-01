package com.livechat.util

/**
 * User: Quang Phúc
 * Date: 2023-05-01
 * Time: 10:43 PM
 */
object NumberUtil {

    fun numberTo2DigitsNumber(number: Int): String {
        return if (number in 0..9) {
            "0$number"
        } else {
            number.toString()
        }
    }
}
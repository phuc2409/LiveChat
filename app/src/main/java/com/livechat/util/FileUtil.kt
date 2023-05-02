package com.livechat.util

/**
 * User: Quang Phúc
 * Date: 2023-05-02
 * Time: 09:56 PM
 */
object FileUtil {

    private fun getRandomChar(): Char {
        return ('a'..'z').random()
    }

    fun getRandomFileName(): String {
        return "${getRandomChar()}${getRandomChar()}${getRandomChar()}${getRandomChar()}${getRandomChar()}"
    }
}
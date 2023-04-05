package com.livechat.extension

import com.google.gson.Gson

/**
 * User: Quang Phúc
 * Date: 2023-02-16
 * Time: 12:15 AM
 */
fun Any.getTag(): String {
    return javaClass.simpleName
}

fun Any?.toJson(): String = Gson().toJson(this)

inline fun <reified T> fromJson(json : String): T {
    return Gson().fromJson(json, T::class.java)
}
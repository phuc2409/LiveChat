package com.livechat.extension

/**
 * User: Quang Phúc
 * Date: 2023-02-16
 * Time: 12:15 AM
 */
fun Any.getTag(): String {
    return javaClass.simpleName
}
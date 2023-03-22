package com.livechat.extension

import android.view.View

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 10:08 PM
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}
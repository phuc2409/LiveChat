package com.livechat.extension

import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.gson.Gson

/**
 * User: Quang Phúc
 * Date: 2023-02-16
 * Time: 12:15 AM
 */
fun Any.getSimpleName(): String {
    return javaClass.simpleName
}

fun Any?.toJson(): String = Gson().toJson(this)

inline fun <reified T> fromJson(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

fun Any?.toStringWithoutQuotationMark(): String {
    return this.toString().replace("\"", "")
}

fun String.highlightEmTag(): Spanned {
    return Html.fromHtml(
        this.replace("<em>", "<span style=\"background-color: yellow;\">")
            .replace("</em>", "</span>"),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )
}

fun String.deleteEmTag() : String {
    return this.replace("<em>", "").replace("</em>", "")
}

package com.livechat.model.api

import com.google.gson.annotations.SerializedName

/**
 * User: Quang Phúc
 * Date: 2023-04-16
 * Time: 10:41 AM
 */
class FcmRequestModel(
    @SerializedName("to")
    val to: String,
    @SerializedName("data")
    val data: Data
) {
    data class Data(
        @SerializedName("chatId")
        val chatId: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
    )
}
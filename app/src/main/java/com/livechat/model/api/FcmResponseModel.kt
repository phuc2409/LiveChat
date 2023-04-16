package com.livechat.model.api

import com.google.gson.annotations.SerializedName

data class FcmResponseModel(
    @SerializedName("canonical_ids")
    val canonicalIds: Int,
    @SerializedName("failure")
    val failure: Int,
    @SerializedName("multicast_id")
    val multicastId: Long,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("success")
    val success: Int
) {
    data class Result(
        @SerializedName("message_id")
        val messageId: String,
        @SerializedName("error")
        val error: String,
    )
}
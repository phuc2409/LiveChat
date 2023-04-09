package com.livechat.model

import com.google.firebase.Timestamp

/**
 * User: Quang Phúc
 * Date: 2023-04-09
 * Time: 12:58 PM
 */
data class MessageModel(
    var id: String = "",
    var chatId: String = "",
    var sendId: String = "",
    var message: String = "",
    var attachmentType: String = "",
    var attachmentUrls: ArrayList<String> = ArrayList(),
    var createdAt: Timestamp? = null,
    var isDeleted: Boolean = false
)

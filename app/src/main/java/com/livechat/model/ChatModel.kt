package com.livechat.model

import com.google.firebase.Timestamp

/**
 * User: Quang Phúc
 * Date: 2023-04-05
 * Time: 09:55 PM
 */
data class ChatModel(
    var id: String = "",
    var participantIds: ArrayList<String> = ArrayList(),
    var participants: ArrayList<ParticipantModel> = ArrayList(),
    var sendId: String = "",
    var sendName: String = "",
    var latestMessage: String = "",
    var updatedAt: Timestamp? = null
) {
    data class ParticipantModel(
        var id: String = "",
        var name: String = "",
        var avatarUrl: String = ""
    )
}

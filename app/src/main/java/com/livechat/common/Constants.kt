package com.livechat.common

/**
 * User: Quang Phúc
 * Date: 2023-03-26
 * Time: 09:04 PM
 */
object Constants {

    object Collections {
        const val USERS = "users"
        const val CHATS = "chats"
        const val MESSAGES = "messages"
    }

    const val FCM_SERVER_KEY =
        "key=AAAAlay7Ju8:APA91bFlgI_CJaHZpEE2J8Eg7lwBJcLAwgFJovU-RjrFXR_cB_V3h5djJ14UqSBSKNBxTMMdHQOvfRdtZPwXL9vlhrb1fMum1Pa24lUUIgYq1Zkz3RRFGdUwEAM3hWhdeQucqIiCCODv"

    const val USER_MODEL = "user_model"
    const val CHAT_MODEL = "chat_model"

    const val MESSAGE_CHANNEL_ID = "message_channel"
    const val INCOMING_CALL_CHANNEL_ID = "incoming_call_channel"

    const val MAX_FILE_SIZE = 20 * 1024 * 1024

    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"

    const val KEY_TITLE = "TITLE"
    const val KEY_CHAT_ID = "CHAT_ID"

    const val KEY_FINISH_INCOMING_CALL_ACTIVITY = "FINISH_INCOMING_CALL_ACTIVITY"
}
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
    const val AGORA_APP_ID = "2bbf7eaa3ade48d9bd70074e5a9c477d"
    const val MAPS_API_KEY = "AIzaSyA5BuKAxAMbwUIE7KYw6lgzgzyeJRnf-a8"
    const val ALGOLIA_APPLICATION_ID = "NBBHPSCPW3"
    const val ALGOLIA_API_KEY = "e58c04e581bfcdccc597157daee9a6f7"

    const val MAPS_DEFAULT_ZOOM = 15f

    const val USER_MODEL = "user_model"
    const val CHAT_MODEL = "chat_model"

    const val MESSAGE_CHANNEL_ID = "message_channel"
    const val INCOMING_CALL_CHANNEL_ID = "incoming_call_channel"

    const val MAX_FILE_SIZE = 50 * 1024 * 1024

    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"

    const val KEY_TITLE = "TITLE"
    const val KEY_CHAT_ID = "CHAT_ID"
    const val KEY_AVATAR_URL = "AVATAR_URL"
    const val KEY_LAT = "LAT"
    const val KEY_LNG = "LNG"
    const val KEY_NAME = "NAME"
    const val KEY_ADDRESS = "ADDRESS"

    const val INCOMING_VIDEO_CALL_DELAY = 20 * 1000

    const val KEY_ACCEPT_VIDEO_CALL = "ACCEPT_VIDEO_CALL"
    const val KEY_DECLINE_VIDEO_CALL = "DECLINE_VIDEO_CALL"
    const val KEY_FINISH_INCOMING_CALL_ACTIVITY = "FINISH_INCOMING_CALL_ACTIVITY"
    const val KEY_STOP_INCOMING_CALL_SERVICE = "KEY_STOP_INCOMING_CALL_SERVICE"
}

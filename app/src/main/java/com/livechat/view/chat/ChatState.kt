package com.livechat.view.chat

import com.livechat.model.ChatModel

/**
 * User: Quang Phúc
 * Date: 2023-04-05
 * Time: 10:18 PM
 */
class ChatState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = ChatState(Status.LOADING)

        fun getChatSuccess(chatModel: ChatModel?) = ChatState(Status.GET_CHAT_SUCCESS, chatModel)

        fun getChatError(e: Exception) = ChatState(Status.GET_CHAT_ERROR, e)
    }

    enum class Status {
        LOADING,
        GET_CHAT_SUCCESS,
        GET_CHAT_ERROR
    }
}
package com.livechat.view.chat

import com.livechat.model.ChatModel
import com.livechat.model.MessageModel

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

        fun sendMessageSuccess() = ChatState(Status.SEND_MESSAGE_SUCCESS)

        fun sendMessageError(e: Exception) = ChatState(Status.SEND_MESSAGE_ERROR, e)

        fun updateMessages(messages: ArrayList<MessageModel>) =
            ChatState(Status.UPDATE_MESSAGES, messages)
    }

    enum class Status {
        LOADING,
        GET_CHAT_SUCCESS,
        GET_CHAT_ERROR,
        SEND_MESSAGE_SUCCESS,
        SEND_MESSAGE_ERROR,
        UPDATE_MESSAGES
    }
}
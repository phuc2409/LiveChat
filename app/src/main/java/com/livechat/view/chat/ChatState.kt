package com.livechat.view.chat

import com.livechat.model.ChatModel
import com.livechat.model.MessageModel
import com.livechat.model.UserModel

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

        fun loadingMedia() = ChatState(Status.LOADING_MEDIA)

        fun getChatSuccess(chatModel: ChatModel?) = ChatState(Status.GET_CHAT_SUCCESS, chatModel)

        fun getChatError(e: Exception) = ChatState(Status.GET_CHAT_ERROR, e)

        fun getUsersSuccess(users: ArrayList<UserModel>) =
            ChatState(Status.GET_USERS_SUCCESS, users)

        fun getUsersError(e: Exception) = ChatState(Status.GET_USERS_ERROR, e)

        fun sendMessageSuccess() = ChatState(Status.SEND_MESSAGE_SUCCESS)

        fun sendMessageError(e: Exception) = ChatState(Status.SEND_MESSAGE_ERROR, e)

        fun updateMessages(messages: ArrayList<MessageModel>) =
            ChatState(Status.UPDATE_MESSAGES, messages)

        fun deleteMessageError(e: Exception) = ChatState(Status.DELETE_MESSAGE_ERROR, e)

        fun blockUser(isBlock: Boolean) = ChatState(Status.BLOCK_USER, isBlock)
    }

    enum class Status {
        LOADING,
        LOADING_MEDIA,
        GET_CHAT_SUCCESS,
        GET_CHAT_ERROR,
        GET_USERS_SUCCESS,
        GET_USERS_ERROR,
        SEND_MESSAGE_SUCCESS,
        SEND_MESSAGE_ERROR,
        UPDATE_MESSAGES,
        DELETE_MESSAGE_ERROR,
        BLOCK_USER
    }
}
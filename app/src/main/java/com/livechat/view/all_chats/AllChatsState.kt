package com.livechat.view.all_chats

import com.livechat.model.ChatModel

/**
 * User: Quang Phúc
 * Date: 2023-04-09
 * Time: 11:23 PM
 */
class AllChatsState(val status: Status, val data: Any? = null) {

    companion object {

        fun loading() = AllChatsState(Status.LOADING)

        fun updateChats(messages: ArrayList<ChatModel>) =
            AllChatsState(Status.UPDATE_CHATS, messages)
    }

    enum class Status {
        LOADING,
        UPDATE_CHATS
    }
}
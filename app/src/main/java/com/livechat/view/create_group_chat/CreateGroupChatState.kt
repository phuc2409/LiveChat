package com.livechat.view.create_group_chat

import com.livechat.model.ChatModel

/**
 * User: Quang Phúc
 * Date: 2023-06-19
 * Time: 12:56 AM
 */
class CreateGroupChatState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun getContactsSuccess(contacts: ArrayList<ChatModel>) =
            CreateGroupChatState(Status.GET_CONTACTS_SUCCESS, contacts)

        fun getContactsError(e: Exception) = CreateGroupChatState(Status.GET_CONTACTS_ERROR, e)

        fun chooseContactSuccess(position: Int) =
            CreateGroupChatState(Status.CHOOSE_CONTACT_SUCCESS, position)

        fun createGroupChatSuccess() = CreateGroupChatState(Status.CREATE_GROUP_CHAT_SUCCESS)

        fun createGroupChatError(e: Exception) =
            CreateGroupChatState(Status.CREATE_GROUP_CHAT_ERROR, e)
    }

    enum class Status {
        GET_CONTACTS_SUCCESS,
        GET_CONTACTS_ERROR,
        CHOOSE_CONTACT_SUCCESS,
        CREATE_GROUP_CHAT_SUCCESS,
        CREATE_GROUP_CHAT_ERROR
    }
}

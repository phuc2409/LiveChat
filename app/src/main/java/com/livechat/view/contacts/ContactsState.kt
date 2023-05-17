package com.livechat.view.contacts

import com.livechat.model.ChatModel

/**
 * User: Quang Phúc
 * Date: 2023-05-17
 * Time: 09:01 PM
 */
class ContactsState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun getContactsSuccess(contacts: ArrayList<ChatModel>) =
            ContactsState(Status.GET_CONTACTS_SUCCESS, contacts)

        fun getContactsError(e: Exception) = ContactsState(Status.GET_CONTACTS_ERROR, e)
    }

    enum class Status {
        GET_CONTACTS_SUCCESS,
        GET_CONTACTS_ERROR
    }
}

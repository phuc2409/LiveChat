package com.livechat.view.chat_info

import com.livechat.model.UserPublicInfoModel

/**
 * User: Quang Phúc
 * Date: 2023-06-04
 * Time: 10:47 PM
 */
class ChatInfoState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun getUserInfoSuccess(userPublicInfoModel: UserPublicInfoModel) = ChatInfoState(
            Status.GET_USER_INFO_SUCCESS,
            userPublicInfoModel
        )

        fun getUserInfoError(e: Exception) = ChatInfoState(Status.GET_USER_INFO_ERROR, e)
    }

    enum class Status {
        GET_USER_INFO_SUCCESS,
        GET_USER_INFO_ERROR
    }
}
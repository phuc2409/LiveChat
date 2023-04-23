package com.livechat.view.profile

/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 10:54 PM
 */
class ProfileState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = ProfileState(Status.LOADING)

        fun updateFullNameSuccess(newFullName: String) = ProfileState(
            Status.UPDATE_FULL_NAME_SUCCESS,
            newFullName
        )

        fun updateFullNameError(e: Exception) = ProfileState(Status.UPDATE_FULL_NAME_ERROR, e)
    }

    enum class Status {
        LOADING,
        UPDATE_FULL_NAME_SUCCESS,
        UPDATE_FULL_NAME_ERROR
    }
}
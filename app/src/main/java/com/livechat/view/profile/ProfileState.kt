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

        fun updateAvatarSuccess() = ProfileState(Status.UPDATE_AVATAR_SUCCESS)

        fun updateAvatarError(e: Exception) = ProfileState(Status.UPDATE_AVATAR_ERROR, e)

        fun updateUserNameSuccess() = ProfileState(Status.UPDATE_USER_NAME_SUCCESS)

        fun updateUserNameError(e: Exception) = ProfileState(Status.UPDATE_USER_NAME_ERROR, e)

        fun userNameExists() = ProfileState(Status.USER_NAME_EXISTS)

        fun updatePasswordSuccess() = ProfileState(Status.UPDATE_PASSWORD_SUCCESS)

        fun updatePasswordError(e: Exception) = ProfileState(Status.UPDATE_PASSWORD_ERROR, e)
    }

    enum class Status {
        LOADING,
        UPDATE_FULL_NAME_SUCCESS,
        UPDATE_FULL_NAME_ERROR,
        UPDATE_AVATAR_SUCCESS,
        UPDATE_AVATAR_ERROR,
        UPDATE_USER_NAME_SUCCESS,
        UPDATE_USER_NAME_ERROR,
        USER_NAME_EXISTS,
        UPDATE_PASSWORD_SUCCESS,
        UPDATE_PASSWORD_ERROR
    }
}
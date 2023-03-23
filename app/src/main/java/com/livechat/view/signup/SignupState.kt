package com.livechat.view.signup

/**
 * User: Quang Phúc
 * Date: 2023-03-23
 * Time: 11:36 PM
 */
class SignupState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = SignupState(Status.LOADING)

        fun signupSuccess() = SignupState(Status.SIGNUP_SUCCESS)

        fun signupError(e: Exception) = SignupState(Status.SIGNUP_ERROR, e)
    }

    enum class Status {
        LOADING,
        SIGNUP_SUCCESS,
        SIGNUP_ERROR
    }
}
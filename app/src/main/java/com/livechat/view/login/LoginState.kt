package com.livechat.view.login

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 09:14 PM
 */
class LoginState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = LoginState(Status.LOADING)

        fun loginSuccess() = LoginState(Status.LOGIN_SUCCESS)

        fun loginError(e: Exception) = LoginState(Status.LOGIN_ERROR, e)
    }

    enum class Status {
        LOADING,
        LOGIN_SUCCESS,
        LOGIN_ERROR
    }
}
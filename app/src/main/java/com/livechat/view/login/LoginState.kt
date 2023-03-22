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

        fun loginSuccess() = LoginState(Status.SUCCESS)

        fun loginError(e: Exception) = LoginState(Status.ERROR, e)
    }

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR
    }
}
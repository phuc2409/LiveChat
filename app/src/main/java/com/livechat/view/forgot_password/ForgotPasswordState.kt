package com.livechat.view.forgot_password

/**
 * User: Quang Phúc
 * Date: 2023-04-20
 * Time: 11:48 PM
 */
class ForgotPasswordState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = ForgotPasswordState(Status.LOADING)

        fun sendEmailSuccess() = ForgotPasswordState(Status.SEND_EMAIL_SUCCESS)

        fun sendEmailError(e: Exception) = ForgotPasswordState(Status.SEND_EMAIL_ERROR, e)
    }

    enum class Status {
        LOADING,
        SEND_EMAIL_SUCCESS,
        SEND_EMAIL_ERROR
    }
}
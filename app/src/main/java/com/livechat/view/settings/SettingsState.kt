package com.livechat.view.settings

/**
 * User: Quang Phúc
 * Date: 2023-05-17
 * Time: 10:37 PM
 */
class SettingsState(val status: Status, val data: Any? = null) {

    companion object {

        fun signOutSuccess() = SettingsState(Status.SIGN_OUT_SUCCESS)

        fun signOutError() = SettingsState(Status.SIGN_OUT_ERROR)
    }

    enum class Status {
        SIGN_OUT_SUCCESS,
        SIGN_OUT_ERROR
    }
}
package com.livechat.view.splash

/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 04:34 PM
 */
class SplashState (
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun goToLogin() = SplashState(Status.GO_TO_LOGIN)

        fun goToMain() = SplashState(Status.GO_TO_MAIN)
    }

    enum class Status {
        GO_TO_LOGIN,
        GO_TO_MAIN
    }
}
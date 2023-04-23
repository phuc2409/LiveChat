package com.livechat.common

import com.livechat.model.UserModel

/**
 * User: Quang Phúc
 * Date: 2023-04-20
 * Time: 11:41 PM
 */
object CurrentUser {

    var id = ""
    var email = ""
    var fullName = ""
    var avatarUrl = ""

    fun clear() {
        id = ""
        email = ""
        fullName = ""
        avatarUrl = ""
    }

    fun update(userModel: UserModel) {
        if (userModel.id.isNotBlank()) {
            id = userModel.id
        }
        if (userModel.email.isNotBlank()) {
            email = userModel.email
        }
        if (userModel.fullName.isNotBlank()) {
            fullName = userModel.fullName
        }
        if (userModel.avatarUrl.isNotBlank()) {
            avatarUrl = userModel.avatarUrl
        }
    }
}
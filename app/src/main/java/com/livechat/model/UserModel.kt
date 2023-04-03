package com.livechat.model

import com.google.firebase.Timestamp

/**
 * User: Quang Phúc
 * Date: 2023-04-03
 * Time: 11:19 PM
 */
data class UserModel(
    var id: String = "",
    var email: String = "",
    var userName: String = "",
    var fullName: String = "",
    var avatarUrl: String = "",
    var birthday: Timestamp = Timestamp(0, 0),
    var tokens: ArrayList<Any> = ArrayList(),
    var friends: ArrayList<Any> = ArrayList(),
    var createdAt: Timestamp = Timestamp(0, 0)
)

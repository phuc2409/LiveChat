package com.livechat.model

import com.algolia.search.model.response.ResponseSearch
import com.google.firebase.Timestamp
import com.livechat.extension.toStringWithoutQuotationMark
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.util.Date

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
    var birthday: Timestamp? = null,
    var tokens: ArrayList<String> = ArrayList(),
    var friends: ArrayList<Any> = ArrayList(),
    var createdAt: Timestamp? = null
) {

    constructor(hit: ResponseSearch.Hit) : this() {
        id = hit["objectID"].toStringWithoutQuotationMark()
        email = hit["email"].toStringWithoutQuotationMark()
//        userName = hit["userName"].toStringWithoutQuotationMark()
        userName = hit["_highlightResult"]?.jsonObject?.get("userName")?.jsonObject?.get("value").toStringWithoutQuotationMark()
//        fullName = hit["fullName"].toStringWithoutQuotationMark()
        fullName = hit["_highlightResult"]?.jsonObject?.get("fullName")?.jsonObject?.get("value").toStringWithoutQuotationMark()
        avatarUrl = hit["avatarUrl"].toStringWithoutQuotationMark()
        birthday = Timestamp(Date(hit["birthday"].toStringWithoutQuotationMark().toLong()))
        val tokensJson = hit.json["tokens"]?.jsonArray
        tokensJson?.let {
            for (i in 0 until  it.size) {
                tokens.add(it[i].toStringWithoutQuotationMark())
            }
        }
        createdAt = Timestamp(Date(hit["createdAt"].toStringWithoutQuotationMark().toLong()))
    }
}

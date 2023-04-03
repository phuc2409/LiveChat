package com.livechat.view.search

import com.livechat.model.UserModel

/**
 * User: Quang Phúc
 * Date: 2023-04-03
 * Time: 01:08 AM
 */
class SearchState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = SearchState(Status.LOADING)

        fun searchSuccess(users: ArrayList<UserModel>) = SearchState(Status.SEARCH_SUCCESS, users)

        fun searchError(e: Exception) = SearchState(Status.SEARCH_ERROR, e)
    }

    enum class Status {
        LOADING,
        SEARCH_SUCCESS,
        SEARCH_ERROR
    }
}
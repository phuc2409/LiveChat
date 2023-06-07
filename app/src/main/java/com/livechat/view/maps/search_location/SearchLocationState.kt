package com.livechat.view.maps.search_location

import com.livechat.model.api.TextSearchResponseModel

/**
 * User: Quang Phúc
 * Date: 2023-06-08
 * Time: 12:02 AM
 */
class SearchLocationState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun loading() = SearchLocationState(Status.LOADING)

        fun searchSuccess(results: ArrayList<TextSearchResponseModel.Result>) =
            SearchLocationState(Status.SEARCH_SUCCESS, results)

        fun searchError(t: Throwable) = SearchLocationState(Status.SEARCH_ERROR, t)
    }

    enum class Status {
        LOADING,
        SEARCH_SUCCESS,
        SEARCH_ERROR
    }
}

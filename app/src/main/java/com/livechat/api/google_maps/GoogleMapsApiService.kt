package com.livechat.api.google_maps

import com.livechat.common.Constants
import com.livechat.model.api.TextSearchResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * User: Quang Phúc
 * Date: 2023-06-07
 * Time: 11:23 PM
 */
interface GoogleMapsApiService {

    @GET("maps/api/place/textsearch/json")
    fun textSearch(
        @Query("query") query: String,
        @Query("pagetoken") pageToken: String = "",
        @Query("key") key: String = Constants.MAPS_API_KEY
    ): Call<TextSearchResponseModel>
}

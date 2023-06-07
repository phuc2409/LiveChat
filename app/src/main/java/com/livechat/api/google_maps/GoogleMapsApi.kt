package com.livechat.api.google_maps

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * User: Quang Phúc
 * Date: 2023-06-07
 * Time: 11:23 PM
 */
object GoogleMapsApi {

    fun apiInstance(): GoogleMapsApiService {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleMapsApiService::class.java)
    }
}

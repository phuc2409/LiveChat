package com.livechat.api.fcm

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * User: Quang Phúc
 * Date: 2023-04-16
 * Time: 10:43 AM
 */
object FcmApi {

    fun apiInstance(): FcmApiService {
        return Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FcmApiService::class.java)
    }
}
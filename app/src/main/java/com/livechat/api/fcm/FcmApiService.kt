package com.livechat.api.fcm

import com.livechat.common.Constants
import com.livechat.model.api.FcmRequestModel
import com.livechat.model.api.FcmResponseModel
import retrofit2.Call
import retrofit2.http.*

/**
 * User: Quang Phúc
 * Date: 2023-04-16
 * Time: 10:32 AM
 */
interface FcmApiService {

    @POST("fcm/send")
    @Headers("Content-Type: application/json", "Authorization: ${Constants.FCM_SERVER_KEY}")
    fun send(@Body body: FcmRequestModel): Call<FcmResponseModel>
}
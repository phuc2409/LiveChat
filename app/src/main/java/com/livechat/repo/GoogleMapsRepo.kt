package com.livechat.repo

import com.livechat.api.google_maps.GoogleMapsApi
import com.livechat.model.api.TextSearchResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-06-07
 * Time: 11:28 PM
 */
@Singleton
class GoogleMapsRepo @Inject constructor() {

    fun getTextSearch(
        keyword: String,
        pageToken: String = "",
        onSuccess: (textSearchResponseModel: TextSearchResponseModel) -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        GoogleMapsApi.apiInstance().textSearch(query = keyword, pageToken = pageToken)
            .enqueue(object : Callback<TextSearchResponseModel> {

                override fun onResponse(
                    call: Call<TextSearchResponseModel>,
                    response: Response<TextSearchResponseModel>
                ) {
                    val textSearchResponseModel = response.body()
                    if (textSearchResponseModel != null) {
                        onSuccess(textSearchResponseModel)
                    } else {
                        onError(Throwable("textSearchResponseModel == null"))
                    }
                }

                override fun onFailure(call: Call<TextSearchResponseModel>, t: Throwable) {
                    t.printStackTrace()
                    onError(t)
                }
            })
    }
}

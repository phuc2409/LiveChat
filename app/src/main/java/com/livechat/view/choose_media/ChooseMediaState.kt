package com.livechat.view.choose_media

import com.livechat.model.FileModel

/**
 * User: Quang Phúc
 * Date: 2023-05-01
 * Time: 10:08 PM
 */
class ChooseMediaState(
    val status: Status,
    val data: Any? = null
) {

    companion object {

        fun getMediaSuccess(media: ArrayList<FileModel>) =
            ChooseMediaState(Status.GET_MEDIA_SUCCESS, media)

        fun chooseMediaSuccess(position: Int) =
            ChooseMediaState(Status.CHOOSE_MEDIA_SUCCESS, position)
    }

    enum class Status {
        GET_MEDIA_SUCCESS,
        CHOOSE_MEDIA_SUCCESS
    }
}
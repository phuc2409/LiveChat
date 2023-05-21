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

        fun maxFileCountReach(maxFileCount: Int) =
            ChooseMediaState(Status.MAX_FILE_COUNT_REACH, maxFileCount)

        fun checkFileSizeSuccess() = ChooseMediaState(Status.CHECK_FILE_SIZE_SUCCESS)

        fun checkFileSizeError() = ChooseMediaState(Status.CHECK_FILE_SIZE_ERROR)
    }

    enum class Status {
        GET_MEDIA_SUCCESS,
        CHOOSE_MEDIA_SUCCESS,
        MAX_FILE_COUNT_REACH,
        CHECK_FILE_SIZE_SUCCESS,
        CHECK_FILE_SIZE_ERROR
    }
}
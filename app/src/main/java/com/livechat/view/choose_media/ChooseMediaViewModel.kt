package com.livechat.view.choose_media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.common.Constants
import com.livechat.helper.MediaHelper
import com.livechat.model.FileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-27
 * Time: 12:05 AM
 */
@HiltViewModel
class ChooseMediaViewModel @Inject constructor(private val media: MediaHelper) : BaseViewModel() {

    private val _state = MutableLiveData<ChooseMediaState>()
    val state: LiveData<ChooseMediaState> = _state

    private var files = ArrayList<FileModel>()
    private var maxFileCount = Int.MAX_VALUE
    private var fileCount = 0

    fun getAllMedia() {
        val images = media.getAllImages()
        val videos = media.getAllVideos()
        images.addAll(videos)
        images.sortByDescending {
            it.dateTaken
        }
        files = images
        _state.postValue(ChooseMediaState.getMediaSuccess(files))
    }

    fun getAllAvatar() {
        maxFileCount = 1
        val images = media.getAllImages()
        images.sortByDescending {
            it.dateTaken
        }
        files = images
        _state.postValue(ChooseMediaState.getMediaSuccess(files))
    }

    fun chooseMedia(position: Int) {
        if (!files[position].isSelected && fileCount >= maxFileCount) {
            _state.postValue(ChooseMediaState.maxFileCountReach(maxFileCount))
            return
        }

        files[position].isSelected = !files[position].isSelected
        fileCount += if (files[position].isSelected) {
            1
        } else {
            -1
        }
        _state.postValue(ChooseMediaState.chooseMediaSuccess(position))
    }

    fun checkFileSize() {
        var size = 0L
        for (i in files) {
            if (i.isSelected) {
                size += i.size
            }
        }
        if (size > Constants.MAX_FILE_SIZE) {
            _state.postValue(ChooseMediaState.checkFileSizeError())
        } else {
            _state.postValue(ChooseMediaState.checkFileSizeSuccess())
        }
    }
}
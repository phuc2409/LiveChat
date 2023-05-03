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

    fun chooseMedia(position: Int) {
        files[position].isSelected = !files[position].isSelected
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
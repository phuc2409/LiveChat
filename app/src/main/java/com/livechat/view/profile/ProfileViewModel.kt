package com.livechat.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
import com.livechat.helper.SharedPreferencesHelper
import com.livechat.model.MessageModel
import com.livechat.repo.FileRepo
import com.livechat.repo.UsersRepo
import com.livechat.util.FileUtil
import com.livechat.util.TimeUtil
import com.livechat.view.chat.ChatState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 11:02 PM
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fileRepo: FileRepo,
    private val usersRepo: UsersRepo,
    private val sharedPrefs: SharedPreferencesHelper
) : BaseViewModel() {

    private val _state = MutableLiveData<ProfileState>()
    val state: LiveData<ProfileState> = _state

    fun updateFullName(fullName: String) {
        usersRepo.updateFullName(fullName,
            onSuccess = {
                CurrentUser.fullName = fullName
                val userModel = sharedPrefs.getCurrentUser()
                userModel.fullName = fullName
                sharedPrefs.setCurrentUser(userModel)
                _state.postValue(ProfileState.updateFullNameSuccess(fullName))
            }, onError = {
                _state.postValue(ProfileState.updateFullNameError(it))
            })
    }

    fun updateAvatarUrl(avatarFilePath: String) {
        // Upload ảnh trước khi đổi avatar
        val extension = File(avatarFilePath).extension
        val newFileName =
            "${TimeUtil.getCurrentTimestamp()}_${FileUtil.getRandomFileName()}.${extension}"
        val newFilePath = "${CurrentUser.id}/$newFileName"
        fileRepo.uploadFile(
            avatarFilePath,
            newFilePath,
            onSuccess = {avatarUrl ->
                usersRepo.updateAvatarUrl(avatarUrl,
                    onSuccess = {
                        CurrentUser.avatarUrl = avatarUrl
                        val userModel = sharedPrefs.getCurrentUser()
                        userModel.avatarUrl = avatarUrl
                        sharedPrefs.setCurrentUser(userModel)
                        _state.postValue(ProfileState.updateAvatarSuccess())
                    }, onError = {
                        _state.postValue(ProfileState.updateAvatarError(it))
                    })
            },
            onError = {
                _state.postValue(ProfileState.updateAvatarError(it))
            }
        )
    }
}
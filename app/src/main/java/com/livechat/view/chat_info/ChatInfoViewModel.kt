package com.livechat.view.chat_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.model.UserPublicInfoModel
import com.livechat.repo.UsersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-06-04
 * Time: 10:47 PM
 */
@HiltViewModel
class ChatInfoViewModel @Inject constructor(private val usersRepo: UsersRepo) : BaseViewModel() {

    private val _state = MutableLiveData<ChatInfoState>()
    val state: LiveData<ChatInfoState> = _state

    private var userPublicInfoModel: UserPublicInfoModel? = null

    fun getUserPublicInfo(userId: String) {
        usersRepo.getUserPublicInfo(userId,
            onSuccess = {
                userPublicInfoModel = it
                _state.postValue(ChatInfoState.getUserInfoSuccess(userPublicInfoModel!!))
            },
            onError = {
                _state.postValue(ChatInfoState.getUserInfoError(it))
            })
    }
}
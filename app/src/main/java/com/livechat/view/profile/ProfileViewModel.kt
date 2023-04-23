package com.livechat.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
import com.livechat.helper.SharedPreferencesHelper
import com.livechat.repo.UsersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 11:02 PM
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
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
}
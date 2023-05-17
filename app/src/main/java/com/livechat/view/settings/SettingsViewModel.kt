package com.livechat.view.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
import com.livechat.helper.SharedPreferencesHelper
import com.livechat.repo.UsersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-05-17
 * Time: 10:38 PM
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val usersRepo: UsersRepo,
    private val auth: FirebaseAuth,
    private val sharedPrefs: SharedPreferencesHelper
) : BaseViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    fun signOut() {
        usersRepo.deleteToken(
            CurrentUser.id, sharedPrefs.getToken(),
            onSuccess = {
                CurrentUser.clear()
                sharedPrefs.deleteCurrentUser()
                auth.signOut()
                _state.postValue(SettingsState.signOutSuccess())
            },
            onError = {
                _state.postValue(SettingsState.signOutError())
            })
    }
}
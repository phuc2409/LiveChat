package com.livechat.view.splash

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
 * Date: 2023-04-23
 * Time: 04:35 PM
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val usersRepo: UsersRepo,
    private val auth: FirebaseAuth,
    private val sharedPrefs: SharedPreferencesHelper
) : BaseViewModel() {

    private val _state = MutableLiveData<SplashState>()
    val state: LiveData<SplashState> = _state

    fun getCurrentUserInfo() {
        if (auth.currentUser == null) {
            CurrentUser.clear()
            _state.postValue(SplashState.goToLogin())
            return
        }

        usersRepo.getUser(auth.currentUser!!.uid,
            onSuccess = {
                CurrentUser.update(it)
                sharedPrefs.setCurrentUser(it)
                _state.postValue(SplashState.goToMain())
            },
            onError = {
                val userModel = sharedPrefs.getCurrentUser()
                if (userModel.id.isBlank()) {
                    auth.signOut()
                    _state.postValue(SplashState.goToLogin())
                } else {
                    CurrentUser.update(userModel)
                    _state.postValue(SplashState.goToMain())
                }
            })
    }
}
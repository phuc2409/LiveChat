package com.livechat.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.helper.SharedPreferencesHelper
import com.livechat.repo.AuthRepo
import com.livechat.repo.UsersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 09:15 PM
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val usersRepo: UsersRepo,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : BaseViewModel() {

    private val _state = MutableLiveData<LoginState>()
    val state: LiveData<LoginState> = _state

    fun login(email: String, password: String) {
        _state.value = LoginState.loading()
        authRepo.login(email, password,
            onSuccess = {
                usersRepo.updateToken(sharedPreferencesHelper.getToken())
                _state.postValue(LoginState.loginSuccess())
            }, onError = {
                _state.value = LoginState.loginError(it)
            })
    }
}
package com.livechat.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
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
    private val auth: FirebaseAuth,
    private val sharedPrefs: SharedPreferencesHelper
) : BaseViewModel() {

    private val _state = MutableLiveData<LoginState>()
    val state: LiveData<LoginState> = _state

    fun login(email: String, password: String) {
        // login -> getUser -> updateToken

        _state.value = LoginState.loading()
        authRepo.login(email, password,
            onSuccess = { userId ->
                usersRepo.getUser(userId,
                    onSuccess = { userModel ->
                        usersRepo.updateToken(sharedPrefs.getToken(),
                            onSuccess = {
                                CurrentUser.update(userModel)
                                sharedPrefs.setCurrentUser(userModel)
                                _state.postValue(LoginState.loginSuccess())
                            },
                            onError = {
                                deleteCurrentUser()
                                _state.value = LoginState.loginError(it)
                            })
                    },
                    onError = {
                        deleteCurrentUser()
                        _state.value = LoginState.loginError(it)
                    })
            }, onError = {
                _state.value = LoginState.loginError(it)
            })
    }

    private fun deleteCurrentUser() {
        auth.signOut()
    }
}
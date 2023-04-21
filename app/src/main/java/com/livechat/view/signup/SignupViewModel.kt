package com.livechat.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.repo.AuthRepo
import com.livechat.repo.UsersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-23
 * Time: 11:38 PM
 */
@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val usersRepo: UsersRepo
) : BaseViewModel() {

    private val _state = MutableLiveData<SignupState>()
    val state: LiveData<SignupState> = _state

    fun signup(email: String, password: String) {
        _state.value = SignupState.loading()
        authRepo.signup(email, password,
            onSuccess = {
                usersRepo.createUser(
                    onSuccess = {
                        _state.postValue(SignupState.signupSuccess())
                    },
                    onError = {
                        _state.postValue(SignupState.signupError(it))
                    })

            }, onError = {
                _state.postValue(SignupState.signupError(it))
            })
    }
}
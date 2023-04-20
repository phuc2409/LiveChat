package com.livechat.view.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-20
 * Time: 11:49 PM
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val authRepo: AuthRepo) :
    BaseViewModel() {

    private val _state = MutableLiveData<ForgotPasswordState>()
    val state: LiveData<ForgotPasswordState> = _state

    fun sendPasswordResetEmail(email: String) {
        _state.postValue(ForgotPasswordState.loading())
        authRepo.sendPasswordResetEmail(email,
            onSuccess = {
                _state.postValue(ForgotPasswordState.sendEmailSuccess())
            }, onError = {
                _state.value = ForgotPasswordState.sendEmailError(it)
            })
    }
}
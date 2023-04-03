package com.livechat.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.model.UserModel
import com.livechat.repo.UsersRepo
import com.livechat.view.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-03
 * Time: 01:09 AM
 */
@HiltViewModel
class SearchViewModel @Inject constructor(private val usersRepo: UsersRepo) : BaseViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private var users = ArrayList<UserModel>()

    fun findUsers(keyword: String) {
        _state.postValue(SearchState.loading())
        usersRepo.findUsers(keyword,
            onSuccess = {
                users = it
                _state.postValue(SearchState.searchSuccess(users))
            }, onError = {
                _state.postValue(SearchState.searchError(it))
            })
    }
}
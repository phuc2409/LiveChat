package com.livechat.view.all_chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
import com.livechat.helper.SharedPreferencesHelper
import com.livechat.model.ChatModel
import com.livechat.repo.ChatsRepo
import com.livechat.repo.UsersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-09
 * Time: 11:24 PM
 */
@HiltViewModel
class AllChatsViewModel @Inject constructor(
    private val chatsRepo: ChatsRepo,
    private val usersRepo: UsersRepo,
    private val auth: FirebaseAuth,
    private val sharedPrefs: SharedPreferencesHelper
) : BaseViewModel() {

    private val _state = MutableLiveData<AllChatsState>()
    val state: LiveData<AllChatsState> = _state

    private var chats: ArrayList<ChatModel> = ArrayList()

    fun startChatsListener() {
        chatsRepo.startChatsListener(
            onSuccess = {
                chats = it
                _state.postValue(AllChatsState.updateChats(chats))
            })
    }

    override fun onCleared() {
        super.onCleared()
        chatsRepo.removeChatsListener()
    }
}
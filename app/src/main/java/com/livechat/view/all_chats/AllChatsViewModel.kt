package com.livechat.view.all_chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.model.ChatModel
import com.livechat.repo.ChatsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-09
 * Time: 11:24 PM
 */
@HiltViewModel
class AllChatsViewModel @Inject constructor(private val chatsRepo: ChatsRepo) : BaseViewModel() {

    private val _state = MutableLiveData<AllChatsState>()
    val state: LiveData<AllChatsState> = _state

    private var chats: ArrayList<ChatModel> = ArrayList()

    fun startChatsListener() {
        chatsRepo.startChatsListener(
            onSuccess = {
                _state.postValue(AllChatsState.updateChats(it))
            })
    }

    override fun onCleared() {
        super.onCleared()
        chatsRepo.removeChatsListener()
    }
}
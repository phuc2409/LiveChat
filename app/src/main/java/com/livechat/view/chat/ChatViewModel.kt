package com.livechat.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseViewModel
import com.livechat.model.ChatModel
import com.livechat.model.UserModel
import com.livechat.repo.ChatsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-05
 * Time: 09:50 PM
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatsRepo: ChatsRepo,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    private val _state = MutableLiveData<ChatState>()
    val state: LiveData<ChatState> = _state

    private lateinit var userModel: UserModel
    private var chatModel: ChatModel? = null

    fun getChatBySearchUser(userModel: UserModel) {
        _state.value = ChatState.loading()

        this.userModel = userModel
        val participantIds = arrayListOf(firebaseAuth.currentUser?.uid ?: "", userModel.id)
        participantIds.sort()

        chatsRepo.getChatByParticipantIds(participantIds,
            onSuccess = {
                chatModel = it
                _state.postValue(ChatState.getChatSuccess(chatModel))
            }, onError = {
                _state.postValue(ChatState.getChatError(it))
            })
    }

    fun sendMessage(message: String) {
        if (chatModel == null) {
            chatsRepo.createChat(
                userModel = userModel,
                message = message,
                onSuccess = {
                    chatModel = it
                    sendMessage(message)
                }, onError = {

                }
            )
        } else {
            chatsRepo.sendMessage(
                chatModel = chatModel!!,
                message = message,
                onSuccess = {
                    chatsRepo.updateChat(
                        chatModel = chatModel!!,
                        message = message,
                        onSuccess = {

                        }, onError = {

                        })
                    _state.postValue(ChatState.sendMessageSuccess())
                }, onError = {
                    _state.postValue(ChatState.sendMessageError(it))
                })
        }
    }
}
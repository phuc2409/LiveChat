package com.livechat.view.chat

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseViewModel
import com.livechat.model.ChatModel
import com.livechat.model.MessageModel
import com.livechat.model.UserModel
import com.livechat.repo.ChatsRepo
import com.livechat.repo.UsersRepo
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
    private val usersRepo: UsersRepo,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    private val _state = MutableLiveData<ChatState>()
    val state: LiveData<ChatState> = _state

    private var chatModel: ChatModel? = null

    private var users = ArrayList<UserModel>()
    private var messages = ArrayList<MessageModel>()

    fun getChatBySearchUser(userModel: UserModel) {
        _state.value = ChatState.loading()

        users.clear()
        users.add(userModel)
        val participantIds = arrayListOf(firebaseAuth.currentUser?.uid ?: "", userModel.id)
        participantIds.sort()

        chatsRepo.getChatByParticipantIds(participantIds,
            onSuccess = {
                chatModel = it
                startMessagesListener()
                startUsersInChatListener()
                _state.postValue(ChatState.getChatSuccess(chatModel))
            }, onError = {
                _state.postValue(ChatState.getChatError(it))
            })
    }

    fun sendMessage(message: String) {
        if (chatModel == null) {
            chatsRepo.createChat(
                userModel = users[0],
                message = message,
                onSuccess = {
                    chatModel = it
                    startMessagesListener()
                    startUsersInChatListener()
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
                    sendNotification(message)
                    _state.postValue(ChatState.sendMessageSuccess())
                }, onError = {
                    _state.postValue(ChatState.sendMessageError(it))
                })
        }
    }

    private fun sendNotification(message: String) {
        if (firebaseAuth.currentUser == null || chatModel == null) {
            return
        }

        for (i in users) {
            if (i.id != firebaseAuth.currentUser?.uid) {
                for (j in i.tokens) {
                    chatsRepo.sendNotification(
                        token = j,
                        chatId = chatModel!!.id,
                        userId = i.id,
                        title = chatModel!!.sendName,
                        message = message,
                        onSuccess = {

                        },
                        onError = {

                        }
                    )
                }
            }
        }
    }

    fun startMessagesListener(newChatModel: ChatModel? = null) {
        if (newChatModel != null) {
            chatModel = newChatModel
        }

        if (chatModel == null) {
            return
        }

        chatsRepo.startMessagesListener(
            chatModel = chatModel!!,
            onSuccess = {
                messages = it
                _state.postValue(ChatState.updateMessages(messages))
            })
    }

    fun startUsersInChatListener() {
        if (chatModel == null) {
            return
        }

        usersRepo.startUsersInChatListener(
            chatModel = chatModel!!,
            onSuccess = {
                users = it
                Handler(Looper.getMainLooper()).postDelayed({
                    _state.postValue(ChatState.getUsersSuccess(users))
                }, 100)
            })
    }

    override fun onCleared() {
        super.onCleared()
        chatsRepo.removeMessagesListener()
        usersRepo.removeUsersInChatListener()
    }
}
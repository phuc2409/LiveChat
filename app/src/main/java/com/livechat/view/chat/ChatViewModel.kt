package com.livechat.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
import com.livechat.model.ChatModel
import com.livechat.model.FileModel
import com.livechat.model.LocationModel
import com.livechat.model.MessageModel
import com.livechat.model.MessageType
import com.livechat.model.UserModel
import com.livechat.repo.ChatsRepo
import com.livechat.repo.FileRepo
import com.livechat.repo.UsersRepo
import com.livechat.util.FileUtil
import com.livechat.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-05
 * Time: 09:50 PM
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatsRepo: ChatsRepo,
    private val fileRepo: FileRepo,
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
                startUsersInChatListener()
                _state.postValue(ChatState.getChatSuccess(chatModel))
            }, onError = {
                _state.postValue(ChatState.getChatError(it))
            })
    }

    fun sendMessage(
        message: String,
        media: ArrayList<FileModel> = ArrayList(),
        location: LocationModel? = null,
        type: String = MessageType.TEXT
    ) {
        // Hai người chưa chat với nhau
        if (chatModel == null) {
            chatsRepo.createChat(
                userModel = users[0],
                message = message,
                onSuccess = {
                    chatModel = it
                    startUsersInChatListener()
                    sendMessage(message, media, location, type)
                }, onError = {

                }
            )
        } else {
            // Gửi vị trí
            if (location != null) {
                chatsRepo.sendMessage(
                    chatModel = chatModel!!,
                    message = message,
                    attachments = ArrayList(),
                    location = location,
                    type = type,
                    onSuccess = {
                        chatsRepo.updateChat(
                            chatModel = chatModel!!,
                            message = message,
                            onSuccess = {

                            }, onError = {

                            })
                        sendNotification(message, type)
                        _state.postValue(ChatState.sendMessageSuccess())
                    }, onError = {
                        _state.postValue(ChatState.sendMessageError(it))
                    })
            }
            // Không có media đính kèm
            else if (media.isEmpty()) {
                chatsRepo.sendMessage(
                    chatModel = chatModel!!,
                    message = message,
                    attachments = ArrayList(),
                    type = type,
                    onSuccess = {
                        if (type == MessageType.STOP_INCOMING_CALL_SERVICE) {
                            sendNotification(message, type)
                        } else {
                            chatsRepo.updateChat(
                                chatModel = chatModel!!,
                                message = message,
                                onSuccess = {

                                }, onError = {

                                })
                            sendNotification(message, type)
                            _state.postValue(ChatState.sendMessageSuccess())
                        }
                    }, onError = {
                        _state.postValue(ChatState.sendMessageError(it))
                    })
            }
            // Có media đính kèm
            else {
                var count = 0
                val attachments = ArrayList<MessageModel.AttachmentModel>()

                for (i in media) {
                    val extension = File(i.path).extension
                    val newFileName =
                        "${TimeUtil.getCurrentTimestamp()}_${FileUtil.getRandomFileName()}.${extension}"
                    val newFilePath = "${chatModel?.id}/$newFileName"
                    fileRepo.uploadFile(
                        i.path,
                        newFilePath,
                        onSuccess = {
                            attachments.add(
                                MessageModel.AttachmentModel(
                                    url = it,
                                    name = newFileName,
                                    type = i.type.name,
                                    duration = i.duration
                                )
                            )
                            count++
                            if (count == media.size) {
                                if (attachments.size == media.size) {
                                    chatsRepo.sendMessage(
                                        chatModel = chatModel!!,
                                        message = message,
                                        attachments = attachments,
                                        type = type,
                                        onSuccess = {
                                            chatsRepo.updateChat(
                                                chatModel = chatModel!!,
                                                message = message,
                                                onSuccess = {

                                                }, onError = {

                                                })
                                            sendNotification(message, type)
                                            _state.postValue(ChatState.sendMessageSuccess())
                                        }, onError = { exception ->
                                            _state.postValue(ChatState.sendMessageError(exception))
                                        })
                                } else {
                                    _state.postValue(ChatState.sendMessageError(Exception("Can not upload files")))
                                }
                            }
                        },
                        onError = {
                            count++
                        }
                    )
                }
            }
        }
    }

    private fun sendNotification(message: String, type: String) {
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
                        avatarUrl = CurrentUser.avatarUrl,
                        message = message,
                        type = type,
                        onSuccess = {

                        },
                        onError = {

                        }
                    )
                }
            }
        }
    }

    fun deleteMessage(messageModel: MessageModel) {
        chatsRepo.deleteMessage(messageModel,
            onSuccess = {

            },
            onError = {
                _state.postValue(ChatState.deleteMessageError(it))
            })
    }

    fun blockUser(isBlock: Boolean) {
        if (chatModel == null) {
            return
        }

        chatsRepo.blockUser(chatModel!!, isBlock,
            onSuccess = {
                _state.postValue(ChatState.blockUser(isBlock))
            },
            onError = {

            })
    }

    fun startMessagesListener() {
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

    fun startUsersInChatListener(newChatModel: ChatModel? = null) {
        if (newChatModel != null) {
            chatModel = newChatModel
        }

        if (chatModel == null) {
            return
        }

        usersRepo.startUsersInChatListener(
            chatModel = chatModel!!,
            onSuccess = { userModels ->
                users = userModels
                _state.postValue(ChatState.getUsersSuccess(users))
            })
    }

    override fun onCleared() {
        super.onCleared()
        chatsRepo.removeMessagesListener()
        usersRepo.removeUsersInChatListener()
    }
}
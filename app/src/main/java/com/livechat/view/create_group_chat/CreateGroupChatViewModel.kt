package com.livechat.view.create_group_chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.common.CurrentUser
import com.livechat.model.ChatModel
import com.livechat.repo.ChatsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-06-19
 * Time: 12:58 AM
 */
@HiltViewModel
class CreateGroupChatViewModel @Inject constructor(private val chatsRepo: ChatsRepo) :
    BaseViewModel() {

    private val _state = MutableLiveData<CreateGroupChatState>()
    val state: LiveData<CreateGroupChatState> = _state

    private var contacts = ArrayList<ChatModel>()

    fun getContacts() {
        chatsRepo.getContacts(
            onSuccess = {
                contacts = it
                _state.postValue(CreateGroupChatState.getContactsSuccess(contacts))
            }, onError = {
                _state.postValue(CreateGroupChatState.getContactsError(it))
            })
    }

    fun chooseMedia(position: Int) {
        contacts[position].isSelected = !contacts[position].isSelected
        _state.postValue(CreateGroupChatState.chooseContactSuccess(position))
    }

    fun createGroupChat(groupChatName: String, message: String) {
        val people = ArrayList<ChatModel.ParticipantModel>()
        for (i in contacts) {
            if (!i.isSelected) {
                continue
            }

            for (j in i.participants) {
                if (j.id != CurrentUser.id) {
                    people.add(j)
                }
            }
        }

        chatsRepo.createGroupChat(
            people,
            groupChatName,
            message,
            onSuccess = {
                _state.postValue(CreateGroupChatState.createGroupChatSuccess())
            }, onError = {
                _state.postValue(CreateGroupChatState.createGroupChatError(it))
            })
    }
}

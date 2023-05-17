package com.livechat.view.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.model.ChatModel
import com.livechat.repo.ChatsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-05-17
 * Time: 09:03 PM
 */
@HiltViewModel
class ContactsViewModel @Inject constructor(private val chatsRepo: ChatsRepo) : BaseViewModel() {

    private val _state = MutableLiveData<ContactsState>()
    val state: LiveData<ContactsState> = _state

    private var contacts = ArrayList<ChatModel>()

    fun getContacts() {
        chatsRepo.getContacts(
            onSuccess = {
                contacts = it
                _state.postValue(ContactsState.getContactsSuccess(contacts))
            }, onError = {
                _state.postValue(ContactsState.getContactsError(it))
            })
    }
}

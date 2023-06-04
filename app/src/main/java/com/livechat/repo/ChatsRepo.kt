package com.livechat.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.livechat.api.fcm.FcmApi
import com.livechat.common.Constants
import com.livechat.common.CurrentUser
import com.livechat.extension.getSimpleName
import com.livechat.model.ChatModel
import com.livechat.model.MessageModel
import com.livechat.model.UserModel
import com.livechat.model.api.FcmRequestModel
import com.livechat.model.api.FcmResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-04-05
 * Time: 09:44 PM
 */
@Singleton
class ChatsRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val usersRepo: UsersRepo
) {

    private var chatsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    fun getChatByParticipantIds(
        participantIds: ArrayList<String>,
        onSuccess: (ChatModel?) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firestore.collection(Constants.Collections.CHATS)
            .whereEqualTo("participantIds", participantIds).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onSuccess(null)
                } else {
                    val chatModel = documents.first().toObject(ChatModel::class.java)
                    chatModel.id = documents.first().id
                    Log.i(getSimpleName(), chatModel.toString())
                    onSuccess(chatModel)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    /**
     * @param userModel user đang chat cùng mình
     */
    fun createChat(
        userModel: UserModel,
        message: String,
        onSuccess: (chatModel: ChatModel) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            onError(Exception("Current user uid is null"))
            return
        }
        val participantIds = arrayListOf(userModel.id, uid)
        participantIds.sort()

        val sendName = firebaseAuth.currentUser?.email ?: ""
        val participants = arrayListOf(
            ChatModel.ParticipantModel(
                id = userModel.id,
                name = userModel.fullName,
                avatarUrl = userModel.avatarUrl,
                isShowAcceptLayout = true
            ), ChatModel.ParticipantModel(
                id = uid,
                name = sendName,
                avatarUrl = CurrentUser.avatarUrl,
                isShowAcceptLayout = false
            )
        )
        participants.sortBy {
            it.id
        }

        val chatModel = ChatModel(
            participantIds = participantIds,
            participants = participants,
            sendId = uid,
            sendName = sendName,
            latestMessage = message,
        )

        val hashMap = hashMapOf(
            "participantIds" to participantIds,
            "participants" to participants,
            "sendId" to uid,
            "sendName" to sendName,
            "latestMessage" to message,
            "isGroupChat" to false,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(Constants.Collections.CHATS).add(hashMap)
            .addOnSuccessListener {
                chatModel.id = it.id
                Log.i(getSimpleName(), chatModel.toString())
                onSuccess(chatModel)
            }.addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun updateChat(
        chatModel: ChatModel,
        message: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        for (i in chatModel.participants) {
            if (i.id == CurrentUser.id) {
                i.name = CurrentUser.fullName
                i.avatarUrl = CurrentUser.avatarUrl
                break
            }
        }

        val hashMap = hashMapOf(
            "participants" to chatModel.participants,
            "sendId" to CurrentUser.id,
            "sendName" to CurrentUser.fullName,
            "latestMessage" to message,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(Constants.Collections.CHATS).document(chatModel.id).update(hashMap)
            .addOnSuccessListener {
                chatModel.sendId = CurrentUser.id
                chatModel.sendName = CurrentUser.fullName
                chatModel.latestMessage = message
                Log.i(getSimpleName(), chatModel.toString())
                onSuccess()
            }.addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun sendMessage(
        chatModel: ChatModel,
        message: String,
        attachments: ArrayList<MessageModel.AttachmentModel>,
        type: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        if (chatModel.participants.any { it.isBlock }) {
            onError(Exception("Block"))
            return
        }

        val hashMap = hashMapOf(
            "chatId" to chatModel.id,
            "sendId" to CurrentUser.id,
            "message" to message,
            "attachments" to attachments,
            "type" to type,
            "createdAt" to FieldValue.serverTimestamp(),
            "isDeleted" to false,
        )

        firestore.collection(Constants.Collections.MESSAGES).add(hashMap)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun sendNotification(
        token: String,
        chatId: String,
        userId: String,
        title: String,
        avatarUrl: String,
        message: String,
        type: String,
        onSuccess: () -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        val fcmRequestModel = FcmRequestModel(
            to = token,
            data = FcmRequestModel.Data(
                chatId = chatId,
                title = title,
                avatarUrl = avatarUrl,
                message = message,
                type = type
            )
        )

        FcmApi.apiInstance().send(body = fcmRequestModel)
            .enqueue(object : Callback<FcmResponseModel> {

                override fun onResponse(
                    call: Call<FcmResponseModel>,
                    response: Response<FcmResponseModel>
                ) {
                    val fcmResponseModel = response.body()
                    if (fcmResponseModel != null && fcmResponseModel.failure > 0) {
                        usersRepo.deleteToken(userId, token,
                            onSuccess = {

                            },
                            onError = {

                            })
                    }
                    onSuccess()
                }

                override fun onFailure(call: Call<FcmResponseModel>, t: Throwable) {
                    t.printStackTrace()
                    onError(t)
                }
            })
    }

    fun deleteMessage(
        messageModel: MessageModel,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val hashMap = hashMapOf("isDeleted" to true) as Map<String, Any>

        firestore.collection(Constants.Collections.MESSAGES)
            .document(messageModel.id)
            .update(hashMap)
            .addOnSuccessListener {
                messageModel.isDeleted = true
                Log.i("deleteMessage", messageModel.toString())
                onSuccess()
            }.addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun blockUser(
        chatModel: ChatModel,
        isBlock: Boolean,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        for (i in chatModel.participants) {
            if (i.id == CurrentUser.id) {
                i.isShowAcceptLayout = false
            } else {
                i.isBlock = isBlock
            }
        }

        val hashMap = hashMapOf("participants" to chatModel.participants) as Map<String, Any>

        firestore.collection(Constants.Collections.CHATS).document(chatModel.id).update(hashMap)
            .addOnSuccessListener {
                Log.i("blockUser", chatModel.toString())
                onSuccess()
            }.addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun startMessagesListener(
        chatModel: ChatModel,
        onSuccess: (messages: ArrayList<MessageModel>) -> Unit
    ) {
        removeMessagesListener()

        //todo: đang cập nhật toàn bộ tin nhắn, chỉ cập nhật tin nhắn mới
        messagesListener = firestore.collection(Constants.Collections.MESSAGES)
            .whereEqualTo("chatId", chatModel.id)
            .orderBy("createdAt")
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.e("Message", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val messages = ArrayList<MessageModel>()

                for (i in value) {
                    val message = i.toObject(MessageModel::class.java)
                    message.id = i.id
                    if (!message.isDeleted) {
                        messages.add(message)
                    }
                }
                Log.i(getSimpleName(), messages.toString())
                onSuccess(messages)
            }
    }

    fun removeMessagesListener() {
        messagesListener?.remove()
    }

    fun startChatsListener(onSuccess: (messages: ArrayList<ChatModel>) -> Unit) {
        removeChatsListener()

        //todo: đang cập nhật toàn bộ tin nhắn, chỉ cập nhật tin nhắn mới
        chatsListener = firestore.collection(Constants.Collections.CHATS)
            .whereArrayContains("participantIds", CurrentUser.id)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.e("Chats", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val chats = ArrayList<ChatModel>()

                for (i in value) {
                    val chat = i.toObject(ChatModel::class.java)
                    chat.id = i.id
                    chats.add(chat)
                }
                Log.i("startChatsListener", chats.toString())
                onSuccess(chats)
            }
    }

    fun removeChatsListener() {
        chatsListener?.remove()
    }

    /**
     * Trả về các chats của người dùng. Dùng trong màn Contacts
     */
    fun getContacts(
        onSuccess: (ArrayList<ChatModel>) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firestore.collection(Constants.Collections.CHATS)
            .whereArrayContains("participantIds", CurrentUser.id)
            .get()
            .addOnSuccessListener { documents ->
                val chatModels = ArrayList<ChatModel>()
                for (i in documents) {
                    val chatModel = i.toObject(ChatModel::class.java)
                    chatModel.id = i.id
                    chatModels.add(chatModel)
                }
                chatModels.sortBy {
                    var name = ""
                    for (i in it.participants) {
                        if (i.id != CurrentUser.id) {
                            name = i.name
                            break
                        }
                    }
                    name
                }
                onSuccess(chatModels)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }
}

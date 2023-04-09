package com.livechat.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.livechat.common.Constants
import com.livechat.extension.getTag
import com.livechat.model.ChatModel
import com.livechat.model.UserModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-05
 * Time: 09:44 PM
 */
class ChatsRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

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
                    Log.i(getTag(), chatModel.toString())
                    onSuccess(chatModel)
                }
            }
            .addOnFailureListener {
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
                avatarUrl = userModel.avatarUrl
            ), ChatModel.ParticipantModel(
                id = uid,
                name = sendName,
                avatarUrl = firebaseAuth.currentUser?.photoUrl.toString()
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
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(Constants.Collections.CHATS).add(hashMap)
            .addOnSuccessListener {
                chatModel.id = it.id
                Log.i(getTag(), chatModel.toString())
                onSuccess(chatModel)
            }.addOnFailureListener {
                onError(it)
            }
    }

    fun updateChat(
        chatModel: ChatModel,
        message: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            onError(Exception("Current user uid is null"))
            return
        }

        val sendName = firebaseAuth.currentUser?.email ?: ""
        val hashMap = hashMapOf(
            "sendId" to uid,
            "sendName" to sendName,
            "latestMessage" to message,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(Constants.Collections.CHATS).document(chatModel.id).update(hashMap)
            .addOnSuccessListener {
                chatModel.sendId = uid
                chatModel.sendName = sendName
                chatModel.latestMessage = message
                Log.i(getTag(), chatModel.toString())
                onSuccess()
            }.addOnFailureListener {
                onError(it)
            }
    }

    fun sendMessage(
        chatModel: ChatModel,
        message: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            onError(Exception("Current user uid is null"))
            return
        }

        val hashMap = hashMapOf(
            "chatId" to chatModel.id,
            "sendId" to uid,
            "message" to message,
            "attachmentType" to "",
            "attachmentUrls" to ArrayList<String>(),
            "createdAt" to FieldValue.serverTimestamp(),
            "isDeleted" to false,
        )

        firestore.collection(Constants.Collections.MESSAGES).add(hashMap)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onError(it)
            }
    }
}
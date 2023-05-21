package com.livechat.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.livechat.common.Constants
import com.livechat.common.CurrentUser
import com.livechat.extension.getSimpleName
import com.livechat.model.ChatModel
import com.livechat.model.UserModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-04-03
 * Time: 01:10 AM
 */
@Singleton
class UsersRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private var usersInChatListener: ListenerRegistration? = null

    fun getUser(
        id: String,
        onSuccess: (userModel: UserModel) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firestore.collection(Constants.Collections.USERS)
            .document(id)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(UserModel::class.java)
                if (user == null) {
                    onError(Exception("getUser == null"))
                } else {
                    user.id = it.id
                    onSuccess(user)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun createUser(
        email: String,
        fullName: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        val emailHashCode = email.hashCode()
        val userName = if (emailHashCode < 0) {
            "0${-emailHashCode}"
        } else {
            emailHashCode.toString()
        }

        val model = hashMapOf(
            "email" to email,
            "userName" to userName,
            "fullName" to fullName,
            "avatarUrl" to "",
            "birthday" to FieldValue.serverTimestamp(),
            "tokens" to ArrayList<String>(),
            "friends" to ArrayList<String>(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(Constants.Collections.USERS)
            .document(firebaseAuth.currentUser?.uid.toString())
            .set(model)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun sendEmailVerification() {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                val a = 3
                a.toString()
            }?.addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun findUsers(
        keyword: String,
        onSuccess: (users: ArrayList<UserModel>) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firestore.collection(Constants.Collections.USERS).get()
            .addOnSuccessListener {
                val users = ArrayList<UserModel>()
                it.documents.forEach { document ->
                    if ((document.getString("fullName")?.contains(keyword) == true
                                || document.getString("userName") == keyword
                                || document.getString("email") == keyword)
                        && document.id != CurrentUser.id
                    ) {
                        document.toObject(UserModel::class.java)?.let { user ->
                            user.id = document.id
                            users.add(user)
                        }
                    }
                }
                Log.i(getSimpleName(), users.toString())
                onSuccess(users)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun updateToken(token: String, onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        if (firebaseAuth.currentUser == null) {
            return
        }

        firestore.collection(Constants.Collections.USERS)
            .document(firebaseAuth.currentUser!!.uid)
            .update("tokens", FieldValue.arrayUnion(token))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun deleteToken(
        userId: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        if (firebaseAuth.currentUser == null) {
            return
        }

        firestore.collection(Constants.Collections.USERS)
            .document(userId)
            .update("tokens", FieldValue.arrayRemove(token))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun updateFullName(fullName: String, onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        firestore.collection(Constants.Collections.USERS)
            .document(CurrentUser.id)
            .update("fullName", fullName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun updateAvatarUrl(avatarUrl: String, onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        firestore.collection(Constants.Collections.USERS)
            .document(CurrentUser.id)
            .update("avatarUrl", avatarUrl)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun startUsersInChatListener(
        chatModel: ChatModel,
        onSuccess: (users: ArrayList<UserModel>) -> Unit
    ) {
        removeUsersInChatListener()

        usersInChatListener = firestore.collection(Constants.Collections.USERS)
            .whereIn(FieldPath.documentId(), chatModel.participantIds)
            .orderBy(FieldPath.documentId())
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.e("Message", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val users = ArrayList<UserModel>()

                for (i in value) {
                    val user = i.toObject(UserModel::class.java)
                    user.id = i.id
                    if (user.createdAt == null) {
                        // createdAt được khởi tạo sau khi update user nên thành lắng nghe hai lần
                        return@addSnapshotListener
                    }
                    users.add(user)
                }
                Log.i(getSimpleName(), users.toString())
                onSuccess(users)
            }
    }

    fun removeUsersInChatListener() {
        usersInChatListener?.remove()
    }
}
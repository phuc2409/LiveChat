package com.livechat.repo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.livechat.common.Constants
import com.livechat.extension.getTag
import com.livechat.model.UserModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-03
 * Time: 01:10 AM
 */
class UsersRepo @Inject constructor(private val firebaseFirestore: FirebaseFirestore) {

    fun findUsers(
        keyword: String,
        onSuccess: (users: ArrayList<UserModel>) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseFirestore.collection(Constants.Collections.USERS).get()
            .addOnSuccessListener {
                val users = ArrayList<UserModel>()
                it.documents.forEach { document ->
                    if (document.getString("fullName")?.contains(keyword) == true
                        || document.getString("userName")?.contains(keyword) == true
                    ) {
                        document.toObject(UserModel::class.java)?.let { user ->
                            user.id = document.id
                            users.add(user)
                        }
                    }
                }
                Log.i(getTag(), users.toString())
                onSuccess(users)
            }
            .addOnFailureListener {
                onError(it)
            }
    }
}
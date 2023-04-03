package com.livechat.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.livechat.common.Constants
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 09:54 PM
 */
class AuthRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val e = task.exception as Exception
                    e.printStackTrace()
                    onError(e)
                }
            }
    }

    fun signup(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createUser(onSuccess, onError)
                } else {
                    val e = task.exception as Exception
                    e.printStackTrace()
                    onError(e)
                }
            }
    }

    private fun createUser(onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        val model = hashMapOf(
            "email" to firebaseAuth.currentUser?.email,
            "userName" to firebaseAuth.currentUser?.email,
            "fullName" to "",
            "avatarUrl" to "",
            "birthday" to FieldValue.serverTimestamp(),
            "tokens" to ArrayList<String>(),
            "friends" to ArrayList<String>(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        firebaseFirestore.collection(Constants.Collections.users)
            .document(firebaseAuth.currentUser?.uid.toString())
            .set(model)
            .addOnSuccessListener {
                firebaseAuth.signOut()
                onSuccess()
            }
            .addOnFailureListener {
                firebaseAuth.signOut()
                onError(it)
            }
    }
}
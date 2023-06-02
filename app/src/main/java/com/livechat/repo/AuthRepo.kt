package com.livechat.repo

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 09:54 PM
 */
@Singleton
class AuthRepo @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    fun login(
        email: String,
        password: String,
        onSuccess: (userId: String) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                if (it.user == null) {
                    onError(Exception("Wrong email or password"))
                    return@addOnSuccessListener
                }
                if (it.user?.isEmailVerified == null || it.user?.isEmailVerified == false) {
                    onError(Exception("You have to verify your email first"))
                    return@addOnSuccessListener
                }

                val id = it.user!!.uid
                onSuccess(id)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun signup(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }

    fun updatePassword(
        password: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        firebaseAuth.currentUser?.updatePassword(password)
            ?.addOnSuccessListener {
                onSuccess()
            }
            ?.addOnFailureListener {
                it.printStackTrace()
                onError(it)
            }
    }
}

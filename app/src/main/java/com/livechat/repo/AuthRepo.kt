package com.livechat.repo

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 09:54 PM
 */
class AuthRepo @Inject constructor(private val firebaseAuth: FirebaseAuth) {

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
                    onError(task.exception as Exception)
                }
            }
    }
}
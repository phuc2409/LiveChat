package com.livechat.view.signup

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivitySignupBinding
import com.livechat.extension.getTag
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-02-16
 * Time: 12:10 AM
 */
@AndroidEntryPoint
class SignupActivity : BaseActivity() {

    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {

    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }

    private fun createAccount() {
        auth.createUserWithEmailAndPassword("tinos52790@otanhome.com", "abcd24901")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(getTag(), "createUserWithEmail:success")
                    val user = auth.currentUser
                    Log.i(getTag(), user.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(getTag(), "createUserWithEmail:failure", task.exception)
                }
            }
    }
}
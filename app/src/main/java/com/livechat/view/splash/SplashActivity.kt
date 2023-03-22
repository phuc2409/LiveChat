package com.livechat.view.splash

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivitySplashBinding
import com.livechat.view.login.LoginActivity
import com.livechat.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 08:42 PM
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        goToNextActivity()
    }

    override fun initView() {

    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }

    private fun goToNextActivity() {
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
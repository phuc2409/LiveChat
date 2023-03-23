package com.livechat.view.main

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivityMainBinding
import com.livechat.view.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 10:55 PM
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        binding.tvEmail.text = firebaseAuth.currentUser?.email
    }

    override fun handleListener() {
        binding.tvSignOut.setOnClickListener {
            firebaseAuth.signOut()

            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun observeViewModel() {

    }
}
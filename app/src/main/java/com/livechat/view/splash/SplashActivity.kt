package com.livechat.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivitySplashBinding
import com.livechat.view.login.LoginActivity
import com.livechat.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 08:42 PM
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        viewModel.getCurrentUserInfo()
    }

    override fun initView() {

    }

    override fun handleListener() {

    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                SplashState.Status.GO_TO_LOGIN -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                SplashState.Status.GO_TO_MAIN -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
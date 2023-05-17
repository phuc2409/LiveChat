package com.livechat.view.settings

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivitySettingsBinding
import com.livechat.extension.showSnackBar
import com.livechat.view.profile.ProfileActivity
import com.livechat.view.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 10:24 PM
 */
@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        if (CurrentUser.avatarUrl.isNotBlank()) {
            Glide.with(this).load(CurrentUser.avatarUrl).into(binding.imgAvatar)
        }
        binding.tvName.text = CurrentUser.fullName
        binding.tvEmail.text = CurrentUser.email
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignOut.setOnClickListener {
            viewModel.signOut()
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                SettingsState.Status.SIGN_OUT_SUCCESS -> {
                    val intent = Intent(this, SplashActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                SettingsState.Status.SIGN_OUT_ERROR -> {
                    showSnackBar(binding.root, R.string.sign_out_error)
                }
            }
        }
    }
}
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
        binding.tvEmail.text = CurrentUser.email
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.llProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.llNotifications.setOnClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // for Android 8 and above
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)

            startActivity(intent)
        }

        binding.llSignOut.setOnClickListener {
            viewModel.signOut()
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                SettingsState.Status.SIGN_OUT_SUCCESS -> {
                    val intent = Intent(this, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                SettingsState.Status.SIGN_OUT_ERROR -> {
                    showSnackBar(binding.root, R.string.sign_out_error)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (CurrentUser.avatarUrl.isNotBlank()) {
            Glide.with(this).load(CurrentUser.avatarUrl).centerCrop().into(binding.imgAvatar)
        }
        binding.tvName.text = CurrentUser.fullName
    }
}

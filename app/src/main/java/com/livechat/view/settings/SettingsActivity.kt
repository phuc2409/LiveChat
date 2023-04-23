package com.livechat.view.settings

import android.content.Intent
import android.os.Bundle
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivitySettingsBinding
import com.livechat.view.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 10:24 PM
 */
@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {

    }

    override fun handleListener() {
        binding.tvProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun observeViewModel() {

    }
}
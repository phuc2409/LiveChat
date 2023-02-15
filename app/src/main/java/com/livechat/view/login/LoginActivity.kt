package com.livechat.view.login

import android.os.Bundle
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-02-15
 * Time: 11:16 PM
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {

    }

    override fun handleListener() {
        val a = 4
    }

    override fun observeViewModel() {

    }
}
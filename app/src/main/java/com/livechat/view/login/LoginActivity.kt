package com.livechat.view.login

import android.os.Bundle
import com.livechat.R
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

    private var loginFragment: LoginFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance()
        }
        loginFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentLogin, it).commit()
        }
    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }
}
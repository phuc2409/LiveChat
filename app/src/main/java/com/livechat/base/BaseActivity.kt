package com.livechat.base

import androidx.appcompat.app.AppCompatActivity

/**
 * User: Quang Phúc
 * Date: 2023-02-15
 * Time: 11:18 PM
 */
abstract class BaseActivity : AppCompatActivity() {

    protected fun setupView() {
        initView()
        handleListener()
        observeViewModel()
    }

    protected abstract fun initView()

    protected abstract fun handleListener()

    protected abstract fun observeViewModel()
}
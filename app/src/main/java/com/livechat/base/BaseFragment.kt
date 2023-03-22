package com.livechat.base

import androidx.fragment.app.Fragment

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 08:59 PM
 */
abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected fun setupView() {
        initView()
        handlerListener()
        observeViewModel()
    }

    abstract fun initView()

    abstract fun handlerListener()

    abstract fun observeViewModel()
}
package com.livechat.base

import androidx.fragment.app.Fragment
import com.livechat.view.login.LoginActivity

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 08:59 PM
 */
abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected fun setupView() {
        initView()
        handleListener()
        observeViewModel()
    }

    abstract fun initView()

    abstract fun handleListener()

    abstract fun observeViewModel()

    protected fun getLoginActivity(): LoginActivity? {
        return if (activity == null) {
            null
        } else {
            activity as LoginActivity
        }
    }
}
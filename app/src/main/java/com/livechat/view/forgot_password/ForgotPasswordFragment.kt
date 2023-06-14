package com.livechat.view.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentForgotPasswordBinding
import com.livechat.extension.gone
import com.livechat.extension.showKeyboard
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.util.ValidateUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-20
 * Time: 11:47 PM
 */
@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment(R.layout.fragment_signup) {

    companion object {

        private const val SEND_EMAIL_DELAY = 60 * 1000
        private var lastTimeSendEmail = 0L

        fun newInstance(): ForgotPasswordFragment {
            return ForgotPasswordFragment()
        }
    }

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {
        binding.etEmail.showKeyboard()
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            getLoginActivity()?.pressBack()
        }

        binding.cvReset.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (System.currentTimeMillis() - lastTimeSendEmail < SEND_EMAIL_DELAY) {
                context?.showSnackBar(binding.root, R.string.wait_1_minute)
            } else if (!ValidateUtil.isValidEmail(email)) {
                binding.etEmail.error = getString(R.string.invalid_email_address)
            } else {
                viewModel.sendPasswordResetEmail(email)
            }
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it.status) {

                ForgotPasswordState.Status.LOADING -> {
                    showLoading()
                }

                ForgotPasswordState.Status.SEND_EMAIL_SUCCESS -> {
                    hideLoading()
                    lastTimeSendEmail = System.currentTimeMillis()
                    context?.showSnackBar(binding.root, R.string.send_email_success)
                }

                ForgotPasswordState.Status.SEND_EMAIL_ERROR -> {
                    hideLoading()
                    val e = it.data as Exception
                    e.message?.let { message ->
                        context?.showSnackBar(binding.root, message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.clLoading.visible()
    }

    private fun hideLoading() {
        binding.clLoading.gone()
    }
}

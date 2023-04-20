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
import com.livechat.extension.showToast
import com.livechat.extension.visible
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-20
 * Time: 11:47 PM
 */
@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment(R.layout.fragment_signup) {

    companion object {
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

    }

    override fun handleListener() {
        binding.tvBack.setOnClickListener {
            getLoginActivity()?.pressBack()
        }

        binding.tvReset.setOnClickListener {
            viewModel.sendPasswordResetEmail(binding.etEmail.text.toString())
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

                    if (context == null) {
                        return@observe
                    }
                    requireContext().showToast(R.string.send_email_success)
                }

                ForgotPasswordState.Status.SEND_EMAIL_ERROR -> {
                    hideLoading()

                    if (context == null) {
                        return@observe
                    }
                    val e = it.data as Exception
                    e.message?.let { message ->
                        requireContext().showToast(message)
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
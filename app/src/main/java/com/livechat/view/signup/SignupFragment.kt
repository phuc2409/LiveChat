package com.livechat.view.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentSignupBinding
import com.livechat.extension.gone
import com.livechat.extension.showToast
import com.livechat.extension.visible
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-02-16
 * Time: 12:10 AM
 */
@AndroidEntryPoint
class SignupFragment : BaseFragment(R.layout.fragment_signup) {

    companion object {
        fun newInstance(): SignupFragment {
            return SignupFragment()
        }
    }

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignupViewModel

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]
        binding = FragmentSignupBinding.inflate(layoutInflater)
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
            getLoginActivity()?.closeSignup()
        }

        binding.tvSignup.setOnClickListener {
            viewModel.signup(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it.status) {

                SignupState.Status.LOADING -> {
                    showLoading()
                }

                SignupState.Status.SIGNUP_SUCCESS -> {
                    hideLoading()

                    if (context == null) {
                        return@observe
                    }
                    requireContext().showToast(R.string.signup_success)
                }

                SignupState.Status.SIGNUP_ERROR -> {
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
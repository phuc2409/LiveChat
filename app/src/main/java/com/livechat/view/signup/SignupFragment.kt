package com.livechat.view.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentSignupBinding
import com.livechat.extension.gone
import com.livechat.extension.showKeyboard
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.util.ValidateUtil
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
        binding.etFullName.showKeyboard()
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            getLoginActivity()?.pressBack()
        }

        binding.cvSignUp.setOnClickListener {
            val fullName = binding.etFullName.text
            val email = binding.etEmail.text
            val password = binding.etPassword.text
            val passwordConfirm = binding.etPasswordConfirm.text
            var hasError = false

            // full name
            if (fullName == null || fullName.isBlank()) {
                binding.etFullName.error = getString(R.string.name_is_empty)
                hasError = true
            }
            // email
            if (email == null || email.isBlank()) {
                binding.etEmail.error = getString(R.string.email_is_empty)
                hasError = true
            } else if (!ValidateUtil.isValidEmail(email.toString())) {
                binding.etEmail.error = getString(R.string.invalid_email_address)
                hasError = true
            }
            // password
            if (password == null || password.isBlank()) {
                binding.etPassword.error = getString(R.string.password_is_empty)
                hasError = true
            } else if (!ValidateUtil.isValidPassword(password.toString())) {
                binding.etPassword.error = getString(R.string.password_validate)
                hasError = true
            }
            // password confirm
            if (passwordConfirm == null || passwordConfirm.isBlank()) {
                binding.etPasswordConfirm.error = getString(R.string.password_is_empty)
                hasError = true
            } else if (!ValidateUtil.isValidPassword(passwordConfirm.toString())) {
                binding.etPasswordConfirm.error = getString(R.string.password_validate)
                hasError = true
            } else if (password.toString() != passwordConfirm.toString()) {
                binding.etPasswordConfirm.error = getString(R.string.passwords_do_not_match)
                hasError = true
            }

            if (!hasError) {
                viewModel.signup(fullName.toString(), email.toString(), password.toString())
            }
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

                    binding.tvEmail.text = binding.etEmail.text.toString()
                    binding.clEmailVerify.visible()
                }

                SignupState.Status.SIGNUP_ERROR -> {
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
package com.livechat.view.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentLoginBinding
import com.livechat.extension.gone
import com.livechat.extension.hideKeyboard
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-03-22
 * Time: 08:59 PM
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment(R.id.fragmentLogin) {

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {

    }

    override fun handleListener() {
        binding.cvLogin.setOnClickListener {
            binding.etEmail.hideKeyboard()

            val email = binding.etEmail.text
            val password = binding.etPassword.text
            var hasError = false

            if (email == null || email.isBlank()) {
                binding.etEmail.error = getString(R.string.email_is_empty)
                hasError = true
            }
            if (password == null || password.isBlank()) {
                binding.etPassword.error = getString(R.string.password_is_empty)
                hasError = true
            }
            if (!hasError) {
                viewModel.login(email.toString(), password.toString())
            }
        }

        binding.tvSignup.setOnClickListener {
            getLoginActivity()?.openSignup()
        }

        binding.tvForgotPassword.setOnClickListener {
            getLoginActivity()?.openForgotPassword()
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it.status) {

                LoginState.Status.LOADING -> {
                    showLoading()
                }

                LoginState.Status.LOGIN_SUCCESS -> {
                    hideLoading()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                LoginState.Status.LOGIN_ERROR -> {
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

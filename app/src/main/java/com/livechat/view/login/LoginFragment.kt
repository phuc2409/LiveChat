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
import com.livechat.extension.showToast
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

    override fun handlerListener() {
        binding.tvLogin.setOnClickListener {
            viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it.status) {

                LoginState.Status.LOADING -> {
                    showLoading()
                }

                LoginState.Status.SUCCESS -> {
                    hideLoading()

                    if (context == null) {
                        return@observe
                    }
                    requireContext().showToast(R.string.login_success)

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                LoginState.Status.ERROR -> {
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
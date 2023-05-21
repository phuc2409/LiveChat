package com.livechat.view.profile

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityProfileBinding
import com.livechat.extension.gone
import com.livechat.extension.hideKeyboard
import com.livechat.extension.showKeyboard
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import dagger.hilt.android.AndroidEntryPoint


/**
 * User: Quang Phúc
 * Date: 2023-04-23
 * Time: 10:34 PM
 */
@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding

    private var oldFullName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        oldFullName = CurrentUser.fullName

        setupView()
    }

    override fun initView() {
        binding.tvFullName.text = CurrentUser.fullName
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgEdit.setOnClickListener {
            showEditFullName()
        }

        binding.imgCheck.setOnClickListener {
            binding.etFullName.hideKeyboard()
            val fullName = binding.etFullName.text.toString()
            if (fullName != oldFullName) {
                viewModel.updateFullName(fullName)
            } else {
                hideEditFullName()
            }
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                ProfileState.Status.LOADING -> {

                }

                ProfileState.Status.UPDATE_FULL_NAME_SUCCESS -> {
                    val newFullName = it.data as String
                    oldFullName = newFullName
                    binding.tvFullName.text = newFullName
                    hideEditFullName()
                    showSnackBar(binding.root, R.string.success)
                }

                ProfileState.Status.UPDATE_FULL_NAME_ERROR -> {
                    hideEditFullName()
                    binding.etFullName.setText(oldFullName)
                    showSnackBar(binding.root, R.string.error)
                }
            }
        }
    }

    private fun showEditFullName() {
        binding.imgEdit.gone()
        binding.tvFullName.gone()
        binding.imgCheck.visible()
        binding.etFullName.setText(CurrentUser.fullName)
        binding.etFullName.visible()
        binding.etFullName.showKeyboard()
    }

    private fun hideEditFullName() {
        binding.imgCheck.gone()
        binding.etFullName.gone()
        binding.imgEdit.visible()
        binding.tvFullName.visible()
    }
}

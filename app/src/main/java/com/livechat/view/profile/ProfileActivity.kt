package com.livechat.view.profile

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityProfileBinding
import com.livechat.extension.showToast
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
        binding.etFullName.setText(CurrentUser.fullName)
    }

    override fun handleListener() {
        binding.imgCheck.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            if (fullName != oldFullName) {
                viewModel.updateFullName(fullName)
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
                    showToast(R.string.success)
                }

                ProfileState.Status.UPDATE_FULL_NAME_ERROR -> {
                    binding.etFullName.setText(oldFullName)
                    showToast(R.string.error)
                }
            }
        }
    }
}
package com.livechat.view.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityProfileBinding
import com.livechat.extension.checkPermissions
import com.livechat.extension.copyToClipboard
import com.livechat.extension.gone
import com.livechat.extension.hideKeyboard
import com.livechat.extension.showKeyboard
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.model.FileModel
import com.livechat.util.PermissionsUtil
import com.livechat.view.alert_dialog.UpdatePasswordAlertDialog
import com.livechat.view.alert_dialog.UpdateUserNameAlertDialog
import com.livechat.view.alert_dialog.UserNameAlertDialog
import com.livechat.view.choose_media.ChooseMediaActivity
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

    private val requestStoragePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all {
            it.value
        }
        if (isGranted) {
            chooseMedia()
        } else {
            showSnackBar(binding.root, R.string.can_not_choose_media)
        }
    }

    private val chooseMediaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra(ChooseMediaActivity.KEY_ITEMS)?.let {
                    val type = object : TypeToken<ArrayList<FileModel>>() {}.type
                    val items: ArrayList<FileModel> = Gson().fromJson(it, type)
                    if (items.isNotEmpty()) {
                        binding.clLoading.visible()
                        viewModel.updateAvatarUrl(items.first().path)
                    }
                }
            }
        }

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
        if (CurrentUser.avatarUrl.isNotBlank()) {
            Glide.with(this).load(CurrentUser.avatarUrl).centerCrop().into(binding.imgAvatar)
        }
        binding.tvUserName.text = CurrentUser.userName
        binding.tvEmail.text = CurrentUser.email
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgAvatar.setOnClickListener {
            if (checkPermissions(PermissionsUtil.getStoragePermissions())) {
                chooseMedia()
            } else {
                requestStoragePermissionsLauncher.launch(PermissionsUtil.getStoragePermissions())
            }
        }

        binding.imgEdit.setOnClickListener {
            showEditFullName()
        }

        binding.imgCheck.setOnClickListener {
            binding.etFullName.hideKeyboard()
            val fullName = binding.etFullName.text.toString()
            if (fullName != oldFullName) {
                binding.clLoading.visible()
                viewModel.updateFullName(fullName)
            } else {
                hideEditFullName()
            }
        }

        binding.llUserName.setOnClickListener {
            showUserNameAlertDialog()
        }

        binding.llEmail.setOnClickListener {

        }

        binding.llPassword.setOnClickListener {
            showUpdatePasswordAlertDialog()
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
                    binding.clLoading.gone()
                    hideEditFullName()
                    showSnackBar(binding.root, R.string.success)
                }

                ProfileState.Status.UPDATE_FULL_NAME_ERROR -> {
                    binding.clLoading.gone()
                    hideEditFullName()
                    binding.etFullName.setText(oldFullName)
                    showSnackBar(binding.root, R.string.error)
                }

                ProfileState.Status.UPDATE_AVATAR_SUCCESS -> {
                    binding.clLoading.gone()
                    if (CurrentUser.avatarUrl.isNotBlank()) {
                        Glide.with(this)
                            .load(CurrentUser.avatarUrl)
                            .centerCrop()
                            .into(binding.imgAvatar)
                    }
                }

                ProfileState.Status.UPDATE_AVATAR_ERROR -> {
                    binding.clLoading.gone()
                    showSnackBar(binding.root, R.string.error)
                }

                ProfileState.Status.UPDATE_USER_NAME_SUCCESS -> {
                    binding.clLoading.gone()
                    showSnackBar(binding.root, R.string.success)
                    if (CurrentUser.userName.isNotBlank()) {
                        binding.tvUserName.text = CurrentUser.userName
                    }
                }

                ProfileState.Status.UPDATE_USER_NAME_ERROR -> {
                    binding.clLoading.gone()
                    showSnackBar(binding.root, R.string.error)
                }

                ProfileState.Status.USER_NAME_EXISTS -> {
                    binding.clLoading.gone()
                    showSnackBar(binding.root, R.string.this_user_name_has_already_been_taken)
                }

                ProfileState.Status.UPDATE_PASSWORD_SUCCESS -> {
                    binding.clLoading.gone()
                    showSnackBar(binding.root, R.string.success)
                }

                ProfileState.Status.UPDATE_PASSWORD_ERROR -> {
                    val e = it.data as Exception
                    binding.clLoading.gone()
                    e.message?.let { message ->
                        showSnackBar(binding.root, message)
                    }
                }
            }
        }
    }

    private fun chooseMedia() {
        val intent = Intent(this, ChooseMediaActivity::class.java)
        intent.putExtra(ChooseMediaActivity.KEY_MODE, ChooseMediaActivity.KEY_AVATAR)
        chooseMediaLauncher.launch(intent)
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

    private fun showUserNameAlertDialog() {
        UserNameAlertDialog(this, true, object : UserNameAlertDialog.Listener {

            override fun onCopy() {
                copyToClipboard(CurrentUser.userName)
                showSnackBar(binding.root, R.string.copied_to_clipboard)
            }

            override fun onUpdate() {
                showUpdateUserNameAlertDialog()
            }
        }).show()
    }

    private fun showUpdateUserNameAlertDialog() {
        UpdateUserNameAlertDialog(
            this,
            CurrentUser.userName,
            object : UpdateUserNameAlertDialog.Listener {

                override fun onUpdate(newUserName: String) {
                    binding.clLoading.visible()
                    viewModel.updateUserName(newUserName)
                }
            }).show()
    }

    private fun showUpdatePasswordAlertDialog() {
        UpdatePasswordAlertDialog(this, object : UpdatePasswordAlertDialog.Listener {

            override fun onUpdate(oldPassword: String, newPassword: String) {
                binding.clLoading.visible()
                viewModel.updatePassword(oldPassword, newPassword)
            }
        }).show()
    }
}

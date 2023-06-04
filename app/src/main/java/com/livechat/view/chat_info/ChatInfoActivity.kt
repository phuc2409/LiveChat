package com.livechat.view.chat_info

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityChatInfoBinding
import com.livechat.extension.copyToClipboard
import com.livechat.extension.fromJson
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.model.ChatModel
import com.livechat.model.UserPublicInfoModel
import com.livechat.view.alert_dialog.ConfirmAlertDialog
import com.livechat.view.alert_dialog.UserNameAlertDialog
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-06-04
 * Time: 10:43 PM
 */
@AndroidEntryPoint
class ChatInfoActivity : BaseActivity() {

    companion object {
        const val KEY_CHAT_MODEL = "CHAT_MODEL"
        const val KEY_IS_BLOCK = "IS_BLOCK"
    }

    private lateinit var viewModel: ChatInfoViewModel
    private lateinit var binding: ActivityChatInfoBinding

    private var chatModel: ChatModel? = null
    private var userPublicInfoModel: UserPublicInfoModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatInfoViewModel::class.java]
        binding = ActivityChatInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        val chatModelJson = intent.getStringExtra(KEY_CHAT_MODEL)
        chatModelJson?.let {
            chatModel = fromJson(it)
        }
        if (getOppositeUser()?.id?.isNotBlank() == true) {
            viewModel.getUserPublicInfo(getOppositeUser()?.id!!)
        }
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.llUserName.setOnClickListener {
            showUserNameAlertDialog()
        }

        binding.llBlock.setOnClickListener {
            showConfirmBlock()
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                ChatInfoState.Status.GET_USER_INFO_SUCCESS -> {
                    userPublicInfoModel = it.data as UserPublicInfoModel
                    updateInfo()
                }

                ChatInfoState.Status.GET_USER_INFO_ERROR -> {

                }
            }
        }
    }

    private fun showUserNameAlertDialog() {
        UserNameAlertDialog(this, false, object : UserNameAlertDialog.Listener {

            override fun onCopy() {
                userPublicInfoModel?.userName?.let {
                    copyToClipboard(it)
                    showSnackBar(binding.root, R.string.copied_to_clipboard)
                }
            }

            override fun onUpdate() {

            }
        }).show()
    }

    private fun updateInfo() {
        binding.tvFullName.text = userPublicInfoModel?.fullName
        if (userPublicInfoModel?.avatarUrl?.isNotBlank() == true) {
            Glide.with(this)
                .load(userPublicInfoModel?.avatarUrl)
                .centerCrop()
                .into(binding.imgAvatar)
        }
        binding.tvUserName.text = userPublicInfoModel?.userName

        val isBlock = getOppositeUser()?.isBlock
        if (isBlock == true) {
            binding.tvBlock.setText(R.string.unblock_user)
            binding.llBlock.visible()
        } else if (isBlock == false) {
            binding.tvBlock.setText(R.string.block_user)
            binding.llBlock.visible()
        }
    }

    private fun showConfirmBlock() {
        val isBlock = getOppositeUser()?.isBlock ?: return

        ConfirmAlertDialog(
            this,
            getString(R.string.block_user),
            getString(R.string.block_user_confirm),
            when (isBlock) {

                true -> {
                    getString(R.string.unblock)
                }

                false -> {
                    getString(R.string.block)
                }
            },
            object : ConfirmAlertDialog.Listener {

                override fun onAction() {
                    val intent = Intent()
                    intent.putExtra(KEY_IS_BLOCK, !isBlock)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        ).show()
    }

    private fun getOppositeUser(): ChatModel.ParticipantModel? {
        if (CurrentUser.id.isBlank() || chatModel == null) {
            return null
        }

        for (i in chatModel!!.participants) {
            if (i.id != CurrentUser.id) {
                return i
            }
        }
        return null
    }
}
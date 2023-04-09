package com.livechat.view.chat

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityChatBinding
import com.livechat.extension.fromJson
import com.livechat.extension.showToast
import com.livechat.model.ChatModel
import com.livechat.model.UserModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-04
 * Time: 11:36 PM
 */
@AndroidEntryPoint
class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel

    /**
     * Truyền userModel vào từ màn Search
     */
    private var userModel: UserModel? = null
    private var chatModel: ChatModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userModelJson = intent.getStringExtra(Constants.USER_MODEL)
        userModelJson?.let {
            userModel = fromJson(it)
        }

        setupView()

        userModel?.let {
            viewModel.getChatBySearchUser(it)
        }
    }

    override fun initView() {
        binding.tvChatName.text = userModel?.fullName
    }

    override fun handleListener() {
        binding.tvSend.setOnClickListener {
            viewModel.sendMessage(binding.etChat.text.toString())
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                ChatState.Status.LOADING -> {

                }

                ChatState.Status.GET_CHAT_SUCCESS -> {
                    chatModel = it.data as ChatModel?

                    chatModel?.id
                }

                ChatState.Status.GET_CHAT_ERROR -> {
                    showToast("Get chat error")
                    finish()
                }

                ChatState.Status.SEND_MESSAGE_SUCCESS -> {

                }

                ChatState.Status.SEND_MESSAGE_ERROR -> {

                }
            }
        }
    }
}
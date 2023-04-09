package com.livechat.view.chat

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityChatBinding
import com.livechat.extension.fromJson
import com.livechat.extension.showToast
import com.livechat.model.ChatModel
import com.livechat.model.MessageModel
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

    private var adapter: MessageAdapter? = null

    /**
     * Truyền userModel vào từ màn Search
     */
    private var userModel: UserModel? = null
    private var chatModel: ChatModel? = null

    private var messages: ArrayList<MessageModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userModelJson = intent.getStringExtra(Constants.USER_MODEL)
        userModelJson?.let {
            userModel = fromJson(it)
        }

        val chatModelJson = intent.getStringExtra(Constants.CHAT_MODEL)
        chatModelJson?.let {
            chatModel = fromJson(it)
        }

        setupView()

        userModel?.let {
            viewModel.getChatBySearchUser(it)
        }
        chatModel?.let {
            viewModel.startMessagesListener(it)
        }
    }

    override fun initView() {
        binding.tvChatName.text = userModel?.fullName

//        adapter = MessageAdapter(this, messages) { messageModel, position ->
//
//        }
//        binding.rvChat.adapter = adapter
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
                    binding.etChat.setText("")
                }

                ChatState.Status.SEND_MESSAGE_ERROR -> {

                }

                ChatState.Status.UPDATE_MESSAGES -> {
                    messages = it.data as ArrayList<MessageModel>
                    adapter = MessageAdapter(this, messages) { messageModel, position ->

                    }
                    binding.rvChat.adapter = adapter
//                    adapter?.notifyDataSetChanged()
                    binding.rvChat.scrollToPosition(messages.size - 1)
                }
            }
        }
    }
}
package com.livechat.view.group_chat_info

import android.os.Bundle
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivityGroupChatInfoBinding
import com.livechat.extension.fromJson
import com.livechat.model.ChatModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-06-19
 * Time: 03:12 AM
 */
@AndroidEntryPoint
class GroupChatInfoActivity : BaseActivity() {

    companion object {
        const val KEY_CHAT_MODEL = "CHAT_MODEL"
    }

    private lateinit var binding: ActivityGroupChatInfoBinding

    private var groupChatInfoAdapter: GroupChatInfoAdapter? = null
    private var chatModel: ChatModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        val chatModelJson = intent.getStringExtra(KEY_CHAT_MODEL)
        chatModelJson?.let {
            chatModel = fromJson(it)

            binding.tvGroupName.text = chatModel!!.chatName

            groupChatInfoAdapter = GroupChatInfoAdapter(
                this,
                chatModel!!.participants,
                object : GroupChatInfoAdapter.Listener {

                    override fun onClick(chatModel: ChatModel.ParticipantModel, position: Int) {

                    }
                })
            binding.rvParticipants.adapter = groupChatInfoAdapter
        }
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    override fun observeViewModel() {

    }
}
package com.livechat.view.create_group_chat

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityCreateGroupChatBinding
import com.livechat.extension.gone
import com.livechat.extension.showKeyboard
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.model.ChatModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-06-19
 * Time: 12:15 AM
 */
@AndroidEntryPoint
class CreateGroupChatActivity : BaseActivity() {

    private lateinit var viewModel: CreateGroupChatViewModel
    private lateinit var binding: ActivityCreateGroupChatBinding

    private var adapter: CreateGroupChatAdapter? = null
    private var contacts = ArrayList<ChatModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateGroupChatViewModel::class.java]
        binding = ActivityCreateGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        viewModel.getContacts()
    }

    override fun initView() {
        binding.etName.showKeyboard()
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            val groupName = binding.etName.text.toString()
            if (groupName.isBlank()) {
                showSnackBar(binding.root, R.string.group_name_can_not_blank)
            } else {
                val message = "${CurrentUser.fullName} ${getString(R.string.create_new_group_chat)}"
                binding.clLoading.visible()
                viewModel.createGroupChat(groupName, message)
            }
        }

        binding.clLoading.setOnClickListener {

        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                CreateGroupChatState.Status.GET_CONTACTS_SUCCESS -> {
                    contacts = it.data as ArrayList<ChatModel>
                    binding.progressBar.gone()

                    adapter = CreateGroupChatAdapter(
                        this,
                        contacts,
                        object : CreateGroupChatAdapter.Listener {

                            override fun onClick(chatModel: ChatModel, position: Int) {
                                viewModel.chooseMedia(position)
                            }
                        })
                    binding.rvContacts.adapter = adapter
                }

                CreateGroupChatState.Status.GET_CONTACTS_ERROR -> {

                }

                CreateGroupChatState.Status.CHOOSE_CONTACT_SUCCESS -> {
                    val position = it.data as Int
                    adapter?.notifyItemChanged(position)
                }

                CreateGroupChatState.Status.CREATE_GROUP_CHAT_SUCCESS -> {
                    finish()
                }

                CreateGroupChatState.Status.CREATE_GROUP_CHAT_ERROR -> {
                    binding.clLoading.gone()
                    val e = it.data as Exception
                    e.message?.let { message ->
                        showSnackBar(binding.root, message)
                    }
                }
            }
        }
    }
}

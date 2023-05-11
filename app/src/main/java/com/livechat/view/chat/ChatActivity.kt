package com.livechat.view.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityChatBinding
import com.livechat.extension.checkPermissions
import com.livechat.extension.fromJson
import com.livechat.extension.showToast
import com.livechat.model.ChatModel
import com.livechat.model.FileModel
import com.livechat.model.MessageModel
import com.livechat.model.MessageType
import com.livechat.model.UserModel
import com.livechat.util.PermissionsUtil
import com.livechat.view.bottom_sheet.ChatBottomSheet
import com.livechat.view.choose_media.ChooseMediaActivity
import com.livechat.view.media_viewer.MediaViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-04
 * Time: 11:36 PM
 */
@AndroidEntryPoint
class ChatActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel

    private var adapter: MessageAdapter? = null

    /**
     * Truyền userModel vào từ màn Search
     */
    private var userModels = ArrayList<UserModel>()
    private var chatModel: ChatModel? = null

    private var messages: ArrayList<MessageModel> = ArrayList()

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all {
            it.value
        }
        if (isGranted) {
            showToast("Can post notifications")
        } else {
            showToast("Can't post notifications")
        }
    }

    private val chooseMediaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra(ChooseMediaActivity.KEY_ITEMS)?.let {
                    val type = object : TypeToken<ArrayList<FileModel>>() {}.type
                    val items: ArrayList<FileModel> = Gson().fromJson(it, type)
                    viewModel.sendMessage("[${getString(R.string.media)}]", items)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userModelJson = intent.getStringExtra(Constants.USER_MODEL)
        userModelJson?.let {
            val userModel: UserModel = fromJson(it)
            userModels.add(userModel)
        }

        val chatModelJson = intent.getStringExtra(Constants.CHAT_MODEL)
        chatModelJson?.let {
            chatModel = fromJson(it)
        }

        setupView()

        if (userModels.isNotEmpty()) {
            viewModel.getChatBySearchUser(userModels[0])
        }
        chatModel?.let {
            viewModel.startMessagesListener(it)
            viewModel.startUsersInChatListener()
        }
    }

    override fun initView() {
        setTitle()

//        adapter = MessageAdapter(this, messages) { messageModel, position ->
//
//        }
//        binding.rvChat.adapter = adapter
    }

    override fun handleListener() {
        binding.imgVideoCall.setOnClickListener {
            viewModel.sendMessage("", type = MessageType.INCOMING_VIDEO_CALL)
        }

        binding.imgMore.setOnClickListener {
            ChatBottomSheet(this, object : ChatBottomSheet.Listener {

                override fun onSelectMedia() {
                    if (checkPermissions(PermissionsUtil.getStoragePermissions())) {
                        val intent = Intent(this@ChatActivity, ChooseMediaActivity::class.java)
                        chooseMediaLauncher.launch(intent)
                    } else {
                        requestPermissionsLauncher.launch(PermissionsUtil.getStoragePermissions())
                    }
                }

                override fun onSelectCamera() {

                }
            }).show()
        }

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

                ChatState.Status.GET_USERS_SUCCESS -> {
                    userModels = it.data as ArrayList<UserModel>
                    setTitle()
                }

                ChatState.Status.GET_USERS_ERROR -> {

                }

                ChatState.Status.SEND_MESSAGE_SUCCESS -> {

                }

                ChatState.Status.SEND_MESSAGE_ERROR -> {

                }

                ChatState.Status.UPDATE_MESSAGES -> {
                    messages = it.data as ArrayList<MessageModel>
                    if (messages.isNotEmpty() && messages.last().sendId == CurrentUser.id) {
                        binding.etChat.setText("")
                    }
                    adapter = MessageAdapter(this, messages, object : MessageAdapter.Listener {

                        override fun onAttachmentClick(
                            attachmentModel: MessageModel.AttachmentModel,
                            position: Int
                        ) {
                            val intent = Intent(this@ChatActivity, MediaViewerActivity::class.java)
                            intent.putExtra(MediaViewerActivity.KEY_URL, attachmentModel.url)
                            intent.putExtra(MediaViewerActivity.KEY_FILE_NAME, attachmentModel.name)
                            intent.putExtra(MediaViewerActivity.KEY_TYPE, attachmentModel.type)
                            startActivity(intent)
                        }
                    })
                    binding.rvChat.adapter = adapter
//                    adapter?.notifyDataSetChanged()
                    binding.rvChat.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

    private fun setTitle() {
        binding.tvChatName.text = getOppositeUser()?.fullName
    }

    private fun getOppositeUser(): UserModel? {
        if (firebaseAuth.currentUser == null) {
            return null
        }

        for (i in userModels) {
            if (i.id != firebaseAuth.currentUser?.uid) {
                return i
            }
        }
        return null
    }
}
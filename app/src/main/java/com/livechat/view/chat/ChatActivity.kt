package com.livechat.view.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityChatBinding
import com.livechat.extension.checkPermissions
import com.livechat.extension.copyToClipboard
import com.livechat.extension.fromJson
import com.livechat.extension.getBitmapUri
import com.livechat.extension.gone
import com.livechat.extension.isHavingInternet
import com.livechat.extension.showSnackBar
import com.livechat.extension.showToast
import com.livechat.extension.toJson
import com.livechat.extension.visible
import com.livechat.helper.MediaHelper
import com.livechat.model.ChatModel
import com.livechat.model.EventBusModel
import com.livechat.model.FileModel
import com.livechat.model.LocationModel
import com.livechat.model.MessageModel
import com.livechat.model.MessageType
import com.livechat.model.UserModel
import com.livechat.util.PermissionsUtil
import com.livechat.view.alert_dialog.ConfirmAlertDialog
import com.livechat.view.alert_dialog.MessageAlertDialog
import com.livechat.view.bottom_sheet.ChatBottomSheet
import com.livechat.view.chat_info.ChatInfoActivity
import com.livechat.view.choose_media.ChooseMediaActivity
import com.livechat.view.group_chat_info.GroupChatInfoActivity
import com.livechat.view.maps.MapsActivity
import com.livechat.view.maps.ViewLocationActivity
import com.livechat.view.media_viewer.MediaViewerActivity
import com.livechat.view.video_call.VideoCallActivity
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


/**
 * User: Quang Phúc
 * Date: 2023-04-04
 * Time: 11:36 PM
 */
@AndroidEntryPoint
class ChatActivity : BaseActivity() {

    @Inject
    lateinit var mediaHelper: MediaHelper

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel

    private var adapter: MessageAdapter? = null

    /**
     * Truyền userModel vào từ màn Search
     */
    private var userModels = ArrayList<UserModel>()
    private var chatModel: ChatModel? = null

    private var messages: ArrayList<MessageModel> = ArrayList()
    private var messagesOldSize = 0

    private val chatInfoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getBooleanExtra(ChatInfoActivity.KEY_IS_BLOCK, false)?.let {
                    viewModel.blockUser(it)
                }
            }
        }

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

    private val requestVideoCallPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all {
            it.value
        }
        if (isGranted) {
            sendVideoCallMessage()
        } else {
            showSnackBar(binding.root, R.string.can_not_start_video_call)
        }
    }

    private val requestCameraPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        openCamera()
    }

    private val requestLocationPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        chooseLocation()
    }

    private val chooseMediaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra(ChooseMediaActivity.KEY_ITEMS)?.let {
                    val type = object : TypeToken<ArrayList<FileModel>>() {}.type
                    val items: ArrayList<FileModel> = Gson().fromJson(it, type)
                    viewModel.sendMessage(
                        "[${getString(R.string.media)}]",
                        items,
                        type = MessageType.MEDIA
                    )
                }
            }
        }

    private val openCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val photo = data?.extras?.get("data") as Bitmap
                val tempUri = getBitmapUri(photo)
                val file = mediaHelper.getImageFromUri(tempUri)

                file?.let {
                    viewModel.sendMessage(
                        "[${getString(R.string.media)}]",
                        arrayListOf(it),
                        type = MessageType.MEDIA
                    )
                }
            }
        }

    private val chooseLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val lat = data?.getDoubleExtra(Constants.KEY_LAT, 0.0)
                val lng = data?.getDoubleExtra(Constants.KEY_LNG, 0.0)
                val name = data?.getStringExtra(Constants.KEY_NAME) ?: ""
                val address = data?.getStringExtra(Constants.KEY_ADDRESS) ?: ""

                if (lat != null && lng != null && address.isNotBlank()) {
                    val location = LocationModel(lat, lng, name, address)
                    val message = name.ifBlank {
                        address
                    }
                    viewModel.sendMessage(
                        message = "[${message}]",
                        location = location,
                        type = MessageType.LOCATION
                    )
                }
            }
        }

    @Subscribe
    fun onEvent(event: EventBusModel) {
        if (event.key == Constants.KEY_STOP_INCOMING_CALL_SERVICE) {
            viewModel.sendMessage(
                "",
                type = MessageType.STOP_INCOMING_CALL_SERVICE
            )
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
            viewModel.startUsersInChatListener(it)
        }
    }

    override fun initView() {
        setTitle()

        if (chatModel == null) {
            binding.tvConnect.text =
                "${getString(R.string.send_some_messages)} ${userModels[0].fullName}"
            binding.tvConnect.visible()
        }

//        adapter = MessageAdapter(this, messages) { messageModel, position ->
//
//        }
//        binding.rvChat.adapter = adapter
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.llInfo.setOnClickListener {
            if (chatModel == null) {
                return@setOnClickListener
            }

            if (chatModel!!.isGroupChat) {
                val intent = Intent(this, GroupChatInfoActivity::class.java)
                intent.putExtra(GroupChatInfoActivity.KEY_CHAT_MODEL, chatModel.toJson())
                startActivity(intent)
            } else {
                val intent = Intent(this, ChatInfoActivity::class.java)
                intent.putExtra(ChatInfoActivity.KEY_CHAT_MODEL, chatModel.toJson())
                chatInfoLauncher.launch(intent)
            }
        }

        binding.imgVideoCall.setOnClickListener {
            if (checkPermissions(PermissionsUtil.getVideoCallPermissions())) {
                sendVideoCallMessage()
            } else {
                requestVideoCallPermissionsLauncher.launch(PermissionsUtil.getVideoCallPermissions())
            }
        }

        binding.imgMore.setOnClickListener {
            ChatBottomSheet(this, object : ChatBottomSheet.Listener {

                override fun onSelectMedia() {
                    if (checkPermissions(PermissionsUtil.getStoragePermissions())) {
                        chooseMedia()
                    } else {
                        requestStoragePermissionsLauncher.launch(PermissionsUtil.getStoragePermissions())
                    }
                }

                override fun onSelectCamera() {
                    if (checkPermissions(PermissionsUtil.getCameraPermissions())) {
                        openCamera()
                    } else {
                        requestCameraPermissionsLauncher.launch(PermissionsUtil.getCameraPermissions())
                    }
                }

                override fun onSelectLocation() {
                    if (checkPermissions(PermissionsUtil.getLocationPermissions())) {
                        chooseLocation()
                    } else {
                        requestLocationPermissionsLauncher.launch(PermissionsUtil.getLocationPermissions())
                    }
                }
            }).show()
        }

        binding.etChat.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isBlank()) {
                    binding.imgSend.gone()
                    binding.imgSendDisable.visible()
                } else {
                    binding.imgSendDisable.gone()
                    binding.imgSend.visible()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.imgSend.setOnClickListener {
            val text = binding.etChat.text.toString()
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
            }
        }

        binding.cvAccept.setOnClickListener {
            viewModel.blockUser(false)
        }

        binding.cvBlock.setOnClickListener {
            viewModel.blockUser(true)
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                ChatState.Status.LOADING -> {

                }

                ChatState.Status.LOADING_MEDIA -> {
                    binding.progressBarSendMedia.visible()
                }

                ChatState.Status.GET_CHAT_SUCCESS -> {
                    chatModel = it.data as ChatModel?
                }

                ChatState.Status.GET_CHAT_ERROR -> {
                    showToast("Get chat error")
                    finish()
                }

                ChatState.Status.GET_USERS_SUCCESS -> {
                    userModels = it.data as ArrayList<UserModel>
                    setTitle()
                    showSendMessageLayout()

                    viewModel.startMessagesListener()
                }

                ChatState.Status.GET_USERS_ERROR -> {

                }

                ChatState.Status.SEND_MESSAGE_SUCCESS -> {
                    if (binding.progressBarSendMedia.isVisible) {
                        binding.progressBarSendMedia.gone()
                    }
                }

                ChatState.Status.SEND_MESSAGE_ERROR -> {
                    if (binding.progressBarSendMedia.isVisible) {
                        binding.progressBarSendMedia.gone()
                    }
                }

                ChatState.Status.UPDATE_MESSAGES -> {
                    messages = it.data as ArrayList<MessageModel>
                    if (binding.rvChat.isGone && messages.isNotEmpty()) {
                        binding.tvConnect.gone()
                        binding.rvChat.visible()
                    }
                    if (messages.isNotEmpty() && messages.last().sendId == CurrentUser.id) {
                        binding.etChat.setText("")
                    }
                    adapter = MessageAdapter(
                        this,
                        userModels,
                        messages,
                        object : MessageAdapter.Listener {

                            override fun onSendMessageLongClick(
                                messageModel: MessageModel,
                                position: Int
                            ) {
                                showMessageAlertDialog(
                                    messageModel,
                                    canCopy = true,
                                    canDelete = true
                                )
                            }

                            override fun onReceiveMessageLongClick(
                                messageModel: MessageModel,
                                position: Int
                            ) {
                                showMessageAlertDialog(
                                    messageModel,
                                    canCopy = true,
                                    canDelete = false
                                )
                            }

                            override fun onSendMediaLongClick(
                                messageModel: MessageModel,
                                position: Int
                            ) {
                                showMessageAlertDialog(
                                    messageModel,
                                    canCopy = false,
                                    canDelete = true
                                )
                            }

                            override fun onAttachmentClick(
                                attachmentModel: MessageModel.AttachmentModel,
                                position: Int
                            ) {
                                val intent =
                                    Intent(this@ChatActivity, MediaViewerActivity::class.java)
                                intent.putExtra(MediaViewerActivity.KEY_URL, attachmentModel.url)
                                intent.putExtra(
                                    MediaViewerActivity.KEY_FILE_NAME,
                                    attachmentModel.name
                                )
                                intent.putExtra(MediaViewerActivity.KEY_TYPE, attachmentModel.type)
                                startActivity(intent)
                            }

                            override fun onMapClick(locationModel: LocationModel, position: Int) {
                                val intent =
                                    Intent(this@ChatActivity, ViewLocationActivity::class.java)
                                intent.putExtra(Constants.KEY_LAT, locationModel.lat)
                                intent.putExtra(Constants.KEY_LNG, locationModel.lng)
                                intent.putExtra(Constants.KEY_ADDRESS, locationModel.address)
                                intent.putExtra(Constants.KEY_NAME, locationModel.name)
                                startActivity(intent)
                            }

                            override fun onBindItem(position: Int) {
                                if (position == messages.size - 1) {
                                    viewModel.updateHasRead()
                                }
                            }
                        })
                    binding.rvChat.adapter = adapter
//                    adapter?.notifyDataSetChanged()

                    // Không cuộn xuống cuối recycler view nếu xoá tin nhắn
//                    if (messages.size >= messagesOldSize) {
                    binding.rvChat.scrollToPosition(messages.size - 1)
//                    }
                    messagesOldSize = messages.size
                }

                ChatState.Status.DELETE_MESSAGE_ERROR -> {
                    showSnackBar(binding.root, R.string.error)
                }

                ChatState.Status.BLOCK_USER -> {
                    val isBlock = it.data as Boolean

                    binding.clWantToChat.gone()
                    if (isBlock) {
                        binding.llSendMessage.gone()
                        binding.tvCanNotReply.visible()
                    } else {
                        binding.tvCanNotReply.gone()
                        binding.llSendMessage.visible()
                        binding.imgVideoCall.visible()
                    }
                }
            }
        }
    }

    private fun sendVideoCallMessage() {
        if (VideoCallActivity.hasInstance) {
            showSnackBar(binding.root, R.string.another_call_is_in_progress)
            return
        }

        if (isHavingInternet()) {
            viewModel.sendMessage(
                "[${getString(R.string.call)}]",
                type = MessageType.INCOMING_VIDEO_CALL
            )
            startVideoCall()
        } else {
            showSnackBar(binding.root, R.string.no_internet)
        }
    }

    private fun startVideoCall() {
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_ID, chatModel?.id ?: "")
        intent.putExtra(Constants.KEY_TITLE, getOppositeUser()?.fullName ?: "")
        intent.putExtra(Constants.KEY_AVATAR_URL, getOppositeUser()?.avatarUrl ?: "")
        startActivity(intent)
    }

    private fun chooseMedia() {
        val intent = Intent(this, ChooseMediaActivity::class.java)
        chooseMediaLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCameraLauncher.launch(intent)
    }

    private fun chooseLocation() {
        val intent = Intent(this, MapsActivity::class.java)
        chooseLocationLauncher.launch(intent)
    }

    private fun setTitle() {
        if (chatModel?.isGroupChat == true) {
            binding.tvChatName.text = chatModel?.chatName
            binding.imgAvatar.setImageResource(R.drawable.ic_groups)
            val size = chatModel?.participants?.size ?: 0
            if (size == 1) {
                binding.tvParticipants.text = "$size ${getString(R.string.participant)}"
                binding.tvParticipants.visible()
            } else if (size > 1) {
                binding.tvParticipants.text = "$size ${getString(R.string.participants)}"
                binding.tvParticipants.visible()
            }

            return
        }
        binding.tvChatName.text = getOppositeUser()?.fullName
        getOppositeUser()?.avatarUrl?.let {
            if (it.isNotBlank()) {
                Glide.with(this).load(it).centerCrop().into(binding.imgAvatar)
            }
        }
    }

    /**
     * Xử lý layout gửi tin nhắn khi vừa mở chat
     */
    private fun showSendMessageLayout() {
        var showVideoCall = true
        if (getCurrentParticipant()?.isShowAcceptLayout == true) {
            binding.llSendMessage.gone()
            binding.tvWantToChat.text =
                "${getOppositeUser()?.fullName} ${getString(R.string.want_to_chat_with_you)}"
            binding.clWantToChat.visible()
            binding.tvWantToChat.visible()
        }
        if (chatModel == null) {
            showVideoCall = false
        }
        if (chatModel?.participants?.any { it.isShowAcceptLayout } == true) {
            showVideoCall = false
        }
        if (chatModel?.participants?.any { it.isBlock } == true) {
            binding.llSendMessage.gone()
            binding.tvCanNotReply.visible()
            showVideoCall = false
        }
        if (showVideoCall) {
            binding.imgVideoCall.visible()
        } else {
            binding.imgVideoCall.gone()
        }
    }

    private fun showMessageAlertDialog(
        messageModel: MessageModel,
        canCopy: Boolean,
        canDelete: Boolean
    ) {
        MessageAlertDialog(this, canCopy, canDelete, object : MessageAlertDialog.Listener {

            override fun onCopy() {
                copyToClipboard(messageModel.message)
                showSnackBar(binding.root, R.string.copied_to_clipboard)
            }

            override fun onDelete() {
                ConfirmAlertDialog(
                    this@ChatActivity,
                    getString(R.string.delete_message),
                    getString(R.string.delete_confirm),
                    getString(R.string.delete),
                    object : ConfirmAlertDialog.Listener {

                        override fun onAction() {
                            viewModel.deleteMessage(messageModel)
                        }
                    }
                ).show()
            }
        }).show()
    }

    private fun getOppositeUser(): UserModel? {
        if (CurrentUser.id.isBlank()) {
            return null
        }

        for (i in userModels) {
            if (i.id != CurrentUser.id) {
                return i
            }
        }
        return null
    }

    private fun getCurrentParticipant(): ChatModel.ParticipantModel? {
        if (CurrentUser.id.isBlank() || chatModel == null) {
            return null
        }

        for (i in chatModel!!.participants) {
            if (i.id == CurrentUser.id) {
                return i
            }
        }
        return null
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

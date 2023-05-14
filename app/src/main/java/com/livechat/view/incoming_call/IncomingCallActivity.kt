package com.livechat.view.incoming_call

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityIncomingCallBinding
import com.livechat.extension.checkPermissions
import com.livechat.model.EventBusModel
import com.livechat.service.IncomingCallService
import com.livechat.util.PermissionsUtil
import com.livechat.view.video_call.VideoCallActivity
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * User: Quang Phúc
 * Date: 2023-05-08
 * Time: 10:54 PM
 */
@AndroidEntryPoint
class IncomingCallActivity : BaseActivity() {

    private lateinit var binding: ActivityIncomingCallBinding

    private var chatId = ""
    private var title = ""

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all {
            it.value
        }
        if (isGranted) {
            acceptVideoCall()
        } else {
            Snackbar.make(
                binding.root,
                R.string.do_not_have_camera_and_microphone_permission_to_join_the_call,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    @Subscribe
    fun onEvent(event: EventBusModel) {
        if (event.key == Constants.KEY_FINISH_INCOMING_CALL_ACTIVITY) {
            finishAndRemoveTask()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        chatId = intent.getStringExtra(Constants.KEY_CHAT_ID) ?: ""
        title = intent.getStringExtra(Constants.KEY_TITLE) ?: ""

        binding.tvTitle.text = title
    }

    override fun handleListener() {
        binding.imgAccept.setOnClickListener {
            if (checkPermissions(PermissionsUtil.getVideoCallPermissions())) {
                acceptVideoCall()
            } else {
                requestPermissionsLauncher.launch(PermissionsUtil.getVideoCallPermissions())
            }
        }

        binding.imgDecline.setOnClickListener {
            declineVideoCall()
        }
    }

    override fun observeViewModel() {

    }

    private fun acceptVideoCall() {
        val videoCallIntent = Intent(this, VideoCallActivity::class.java)
        videoCallIntent.putExtra(Constants.KEY_CHAT_ID, chatId)
        videoCallIntent.putExtra(Constants.KEY_TITLE, title)
        startActivity(videoCallIntent)

        val incomingCallServiceIntent = Intent(this, IncomingCallService::class.java)
        stopService(incomingCallServiceIntent)

        finishAndRemoveTask()
    }

    private fun declineVideoCall() {
        val intent = Intent(this, IncomingCallService::class.java)
        stopService(intent)
        finishAndRemoveTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

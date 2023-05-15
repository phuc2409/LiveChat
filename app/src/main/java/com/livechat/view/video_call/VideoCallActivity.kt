package com.livechat.view.video_call

import android.graphics.Color
import android.os.Bundle
import com.livechat.base.BaseActivity
import com.livechat.common.CurrentUser
import com.livechat.databinding.ActivityVideoCallBinding
import com.livechat.extension.checkPermissions
import com.livechat.util.PermissionsUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.agorauikit_android.AgoraButton
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraSettings
import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.rtc2.Constants

/**
 * User: Quang Phúc
 * Date: 2023-05-13
 * Time: 09:51 PM
 */
@AndroidEntryPoint
@OptIn(ExperimentalUnsignedTypes::class)
class VideoCallActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoCallBinding

    private var agView: AgoraVideoViewer? = null

    private var chatId = ""
    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        chatId = intent.getStringExtra(com.livechat.common.Constants.KEY_CHAT_ID) ?: ""
        title = intent.getStringExtra(com.livechat.common.Constants.KEY_TITLE) ?: ""

        binding.tvTitle.text = title

        // Check that the camera and mic permissions are accepted before attempting to join
        if (checkPermissions(PermissionsUtil.getVideoCallPermissions())) {
            try {
                agView = AgoraVideoViewer(
                    this, AgoraConnectionData(com.livechat.common.Constants.AGORA_APP_ID),
                    agoraSettings = settingsWithExtraButtons()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            this.addContentView(agView, binding.clVideoCall.layoutParams)
            joinVideoCall()
        }
    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }

    private fun settingsWithExtraButtons(): AgoraSettings {
        val agoraSettings = AgoraSettings()
        val agBeautyButton = AgoraButton(this)
        agBeautyButton.clickAction = {
            it.isSelected = !it.isSelected
            agBeautyButton.setImageResource(
                if (it.isSelected) {
                    android.R.drawable.star_on
                } else {
                    android.R.drawable.star_off
                }
            )
            it.background.setTint(
                if (it.isSelected) {
                    Color.GREEN
                } else {
                    Color.GRAY
                }
            )
            agView?.agkit?.setBeautyEffectOptions(it.isSelected, agView?.beautyOptions)
        }
        agBeautyButton.setImageResource(android.R.drawable.star_off)

        agoraSettings.extraButtons = mutableListOf(agBeautyButton)

        return agoraSettings
    }

    private fun joinVideoCall() {
        agView?.join(
            chatId,
            role = Constants.CLIENT_ROLE_BROADCASTER,
            uid = CurrentUser.id.hashCode()
        )
    }
}
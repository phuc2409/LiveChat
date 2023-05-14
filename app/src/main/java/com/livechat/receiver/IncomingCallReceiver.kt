package com.livechat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.livechat.common.Constants
import com.livechat.extension.checkPermissions
import com.livechat.service.IncomingCallService
import com.livechat.util.PermissionsUtil
import com.livechat.view.incoming_call.IncomingCallActivity
import com.livechat.view.video_call.VideoCallActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-05-08
 * Time: 11:43 PM
 */
@AndroidEntryPoint
class IncomingCallReceiver : BroadcastReceiver() {

    private var chatId = ""
    private var title = ""

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }
        chatId = intent?.getStringExtra(Constants.KEY_CHAT_ID) ?: ""
        title = intent?.getStringExtra(Constants.KEY_TITLE) ?: ""
        val action = intent?.action ?: ""
        if (action == Constants.KEY_ACCEPT_VIDEO_CALL) {
            // Đủ quyền sẽ bắt đầu cuộc gọi, không đủ sẽ mở màn cuộc gọi đến
            if (context.checkPermissions(PermissionsUtil.getVideoCallPermissions())) {
                acceptVideoCall(context)
            } else {
                openIncomingCall(context)
            }
        } else if (action == Constants.KEY_DECLINE_VIDEO_CALL) {
            declineVideoCall(context)
        }
    }

    private fun acceptVideoCall(context: Context) {
        val videoCallIntent = Intent(context, VideoCallActivity::class.java)
        videoCallIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        videoCallIntent.putExtra(Constants.KEY_CHAT_ID, chatId)
        videoCallIntent.putExtra(Constants.KEY_TITLE, title)
        context.startActivity(videoCallIntent)
        stopIncomingCallService(context)
    }

    private fun openIncomingCall(context: Context) {
        val intent = Intent(context, IncomingCallActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.KEY_CHAT_ID, chatId)
        intent.putExtra(Constants.KEY_TITLE, title)
        context.startActivity(intent)
    }

    private fun declineVideoCall(context: Context) {
        stopIncomingCallService(context)
    }

    private fun stopIncomingCallService(context: Context) {
        val intent = Intent(context, IncomingCallService::class.java)
        context.stopService(intent)
    }
}
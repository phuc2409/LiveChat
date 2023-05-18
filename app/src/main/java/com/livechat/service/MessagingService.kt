package com.livechat.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.livechat.R
import com.livechat.common.Constants
import com.livechat.helper.SharedPreferencesHelper
import com.livechat.model.MessageType
import com.livechat.repo.UsersRepo
import com.livechat.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-04-11
 * Time: 10:50 PM
 */
@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var usersRepo: UsersRepo

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("onNewToken", token)
        sharedPreferencesHelper.setToken(token)
        usersRepo.updateToken(token,
            onSuccess = {

            },
            onError = {

            })
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i("onMessageReceived", message.data.toString())
        val id = message.data["chatId"] ?: ""
        val title = message.data["title"] ?: ""
        val avatarUrl = message.data["avatarUrl"] ?: ""
        val messageText = message.data["message"] ?: ""
        val type = message.data["type"] ?: ""

        if (type == MessageType.INCOMING_VIDEO_CALL) {
            //todo Check thời gian cuộc gọi đến so với thời gian hiện tại
            val intent = Intent(this, IncomingCallService::class.java)
            intent.putExtra(Constants.KEY_CHAT_ID, id)
            intent.putExtra(Constants.KEY_TITLE, title)
            intent.putExtra(Constants.KEY_AVATAR_URL, avatarUrl)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        } else {
            showNotification(id, title, messageText)
        }
    }

    private fun showNotification(id: String, title: String, message: String) {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val mainActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, Constants.MESSAGE_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_chat)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(mainActivityPendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)

        // notificationId is a unique int for each notification that you must define
        if (
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(id.hashCode(), builder.build())
    }
}
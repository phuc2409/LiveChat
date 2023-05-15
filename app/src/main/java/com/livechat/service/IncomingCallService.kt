package com.livechat.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.livechat.R
import com.livechat.common.Constants
import com.livechat.model.EventBusModel
import com.livechat.receiver.IncomingCallReceiver
import com.livechat.view.incoming_call.IncomingCallActivity
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

/**
 * User: Quang Phúc
 * Date: 2023-05-09
 * Time: 12:25 AM
 */
@AndroidEntryPoint
class IncomingCallService : Service() {

    private var ringtone: Ringtone? = null

    private var chatId = ""
    private var title = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        chatId = intent?.getStringExtra(Constants.KEY_CHAT_ID) ?: ""
        title = intent?.getStringExtra(Constants.KEY_TITLE) ?: ""

        val notification: Notification = buildNotification()

        startForeground(1, notification)

        playRingtone()

        return START_NOT_STICKY
    }

    private fun buildNotification(): Notification {
        val acceptIntent = Intent(this, IncomingCallReceiver::class.java)
        acceptIntent.action = Constants.KEY_ACCEPT_VIDEO_CALL
        acceptIntent.putExtra(Constants.KEY_CHAT_ID, chatId)
        acceptIntent.putExtra(Constants.KEY_TITLE, title)
        val acceptPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val declineIntent = Intent(this, IncomingCallReceiver::class.java)
        declineIntent.action = Constants.KEY_DECLINE_VIDEO_CALL
        declineIntent.putExtra(Constants.KEY_CHAT_ID, chatId)
        declineIntent.putExtra(Constants.KEY_TITLE, title)
        val declinePendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val fullScreenIntent = Intent(this, IncomingCallActivity::class.java)
        fullScreenIntent.putExtra(Constants.KEY_CHAT_ID, chatId)
        fullScreenIntent.putExtra(Constants.KEY_TITLE, title)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            2,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationLayout = RemoteViews(packageName, R.layout.notification_incoming_call)
        notificationLayout.setTextViewText(R.id.tvTitle, title)
        notificationLayout.setTextViewText(
            R.id.tvContent,
            "$title ${getString(R.string.is_calling)}"
        )
        notificationLayout.setOnClickPendingIntent(R.id.tvAccept, acceptPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.tvDecline, declinePendingIntent)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, Constants.INCOMING_CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setCustomContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true)

        return notificationBuilder.build()
    }

    private fun playRingtone() {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        ringtone?.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone?.isLooping = true
        }
    }

    private fun stopRingtone() {
        ringtone?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
        EventBus.getDefault().post(EventBusModel(Constants.KEY_FINISH_INCOMING_CALL_ACTIVITY))
    }
}
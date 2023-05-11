package com.livechat.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.livechat.R
import com.livechat.common.Constants
import dagger.hilt.android.HiltAndroidApp

/**
 * User: Quang Phúc
 * Date: 2023-02-15
 * Time: 11:23 PM
 */
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        createMessageNotificationChannel()
        createIncomingCallNotificationChannel()
    }

    private fun createMessageNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.message)
            val description = getString(R.string.message_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.MESSAGE_CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createIncomingCallNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.incoming_call)
            val description = getString(R.string.incoming_call_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.INCOMING_CALL_CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
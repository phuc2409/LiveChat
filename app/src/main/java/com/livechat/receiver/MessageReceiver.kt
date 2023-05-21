package com.livechat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.livechat.view.main.MainActivity
import com.livechat.view.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-05-19
 * Time: 09:59 AM
 */
@AndroidEntryPoint
class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (MainActivity.hasInstance) {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context?.startActivity(mainActivityIntent)
        } else {
            val splashActivityIntent = Intent(context, SplashActivity::class.java)
            splashActivityIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context?.startActivity(splashActivityIntent)
        }
    }
}
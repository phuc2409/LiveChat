package com.livechat.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.livechat.service.IncomingCallService
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-05-08
 * Time: 11:43 PM
 */
@AndroidEntryPoint
class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val incomingIntent = Intent(context, IncomingCallService::class.java)
        context?.stopService(incomingIntent)
    }
}
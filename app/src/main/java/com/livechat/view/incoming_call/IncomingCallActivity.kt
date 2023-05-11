package com.livechat.view.incoming_call

import android.content.Intent
import android.os.Bundle
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityIncomingCallBinding
import com.livechat.model.EventBusModel
import com.livechat.service.IncomingCallService
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

        }

        binding.imgDecline.setOnClickListener {
            val intent = Intent(this, IncomingCallService::class.java)
            stopService(intent)
            finishAndRemoveTask()
        }
    }

    override fun observeViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
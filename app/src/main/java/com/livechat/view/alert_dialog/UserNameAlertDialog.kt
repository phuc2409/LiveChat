package com.livechat.view.alert_dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.livechat.R
import com.livechat.databinding.AlertDialogUserNameBinding
import com.livechat.extension.gone

/**
 * User: Quang Phúc
 * Date: 2023-05-30
 * Time: 10:20 PM
 */
class UserNameAlertDialog(
    context: Context,
    private val canUpdate: Boolean,
    private val listener: Listener
) : AlertDialog(context, R.style.Theme_AlertDialog) {

    interface Listener {

        fun onCopy()

        fun onUpdate()
    }

    private lateinit var binding: AlertDialogUserNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertDialogUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        handleListener()
    }

    private fun initView() {
        if (!canUpdate) {
            binding.tvUpdate.gone()
        }
    }

    private fun handleListener() {
        binding.tvCopy.setOnClickListener {
            listener.onCopy()
            dismiss()
        }

        binding.tvUpdate.setOnClickListener {
            listener.onUpdate()
            dismiss()
        }
    }
}

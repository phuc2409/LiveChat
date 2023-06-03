package com.livechat.view.alert_dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.livechat.R
import com.livechat.databinding.AlertDialogMessageBinding
import com.livechat.extension.gone

/**
 * User: Quang Phúc
 * Date: 2023-06-03
 * Time: 02:53 AM
 */
class MessageAlertDialog(
    context: Context,
    private val canCopy: Boolean,
    private val canDelete: Boolean,
    private val listener: Listener
) : AlertDialog(context, R.style.Theme_AlertDialog) {

    interface Listener {

        fun onCopy()

        fun onDelete()
    }

    private lateinit var binding: AlertDialogMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertDialogMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        handleListener()
    }

    private fun initView() {
        if (!canCopy) {
            binding.tvCopy.gone()
        }

        if (!canDelete) {
            binding.tvDelete.gone()
        }
    }

    private fun handleListener() {
        binding.tvCopy.setOnClickListener {
            listener.onCopy()
            dismiss()
        }

        binding.tvDelete.setOnClickListener {
            listener.onDelete()
            dismiss()
        }
    }
}

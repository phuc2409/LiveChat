package com.livechat.view.alert_dialog


import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.livechat.R
import com.livechat.databinding.AlertDialogConfirmBinding

/**
 * User: Quang Phúc
 * Date: 2023-06-03
 * Time: 03:04 AM
 */
class ConfirmAlertDialog(
    context: Context,
    private val title: String,
    private val content: String,
    private val action: String,
    private val listener: Listener
) : AlertDialog(context, R.style.Theme_AlertDialog) {

    interface Listener {

        fun onAction()
    }

    private lateinit var binding: AlertDialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertDialogConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        handleListener()
    }

    private fun initView() {
        binding.tvTitle.text = title
        binding.tvContent.text = content
        binding.tvAction.text = action
    }

    private fun handleListener() {
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvAction.setOnClickListener {
            listener.onAction()
            dismiss()
        }
    }
}

package com.livechat.view.alert_dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.isGone
import com.livechat.R
import com.livechat.databinding.AlertDialogUpdateUserNameBinding
import com.livechat.extension.gone
import com.livechat.extension.showKeyboard
import com.livechat.extension.visible

/**
 * User: Quang Phúc
 * Date: 2023-05-30
 * Time: 11:35 PM
 */
class UpdateUserNameAlertDialog(
    context: Context,
    private val oldUserName: String,
    private val listener: Listener
) : AppCompatDialog(context, R.style.Theme_AlertDialog) {

    interface Listener {

        fun onUpdate(newUserName: String)
    }

    companion object {
        private const val SHOW_KEYBOARD_DELAYED = 500L
    }

    private lateinit var binding: AlertDialogUpdateUserNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertDialogUpdateUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        handleListener()
    }

    private fun initView() {
        binding.etContent.setText(oldUserName)
        binding.etContent.setSelection(binding.etContent.text.length)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.etContent.showKeyboard()
        }, SHOW_KEYBOARD_DELAYED)
    }

    private fun handleListener() {
        binding.etContent.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text != oldUserName && text.isNotBlank() && text.length > 3) {
                    if (binding.tvUpdate.isGone) {
                        showUpdate()
                    }
                } else {
                    hideUpdate()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvUpdate.setOnClickListener {
            val text = binding.etContent.text.toString()
            listener.onUpdate(text)
            dismiss()
        }
    }

    private fun showUpdate() {
        binding.tvUpdate.visible()
        binding.tvUpdateDisabled.gone()
    }

    private fun hideUpdate() {
        binding.tvUpdate.gone()
        binding.tvUpdateDisabled.visible()
    }
}

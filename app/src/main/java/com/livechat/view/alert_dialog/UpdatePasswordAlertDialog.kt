package com.livechat.view.alert_dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDialog
import com.livechat.R
import com.livechat.databinding.AlertDialogUpdatePasswordBinding
import com.livechat.extension.showKeyboard
import com.livechat.util.ValidateUtil

/**
 * User: Quang Phúc
 * Date: 2023-05-31
 * Time: 12:43 AM
 */
class UpdatePasswordAlertDialog(
    context: Context,
    private val listener: Listener
) : AppCompatDialog(context, R.style.Theme_AlertDialog) {

    interface Listener {

        fun onUpdate(oldPassword: String, newPassword: String)
    }

    companion object {
        private const val SHOW_KEYBOARD_DELAYED = 500L
    }

    private lateinit var binding: AlertDialogUpdatePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertDialogUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        handleListener()
    }

    private fun initView() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.etOldPassword.showKeyboard()
        }, SHOW_KEYBOARD_DELAYED)
    }

    private fun handleListener() {
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvUpdate.setOnClickListener {
            val oldPassword = binding.etOldPassword.text
            val newPassword = binding.etNewPassword.text
            val newPasswordConfirm = binding.etNewPasswordConfirm.text
            var hasError = false

            // old password
            if (oldPassword == null || oldPassword.isBlank()) {
                binding.etOldPassword.error = context.getString(R.string.password_is_empty)
                hasError = true
            } else if (!ValidateUtil.isValidPassword(oldPassword.toString())) {
                binding.etOldPassword.error = context.getString(R.string.password_validate)
                hasError = true
            }
            // password
            if (newPassword == null || newPassword.isBlank()) {
                binding.etNewPassword.error = context.getString(R.string.password_is_empty)
                hasError = true
            } else if (!ValidateUtil.isValidPassword(newPassword.toString())) {
                binding.etNewPassword.error = context.getString(R.string.password_validate)
                hasError = true
            }
            // password confirm
            if (newPasswordConfirm == null || newPasswordConfirm.isBlank()) {
                binding.etNewPasswordConfirm.error = context.getString(R.string.password_is_empty)
                hasError = true
            } else if (!ValidateUtil.isValidPassword(newPasswordConfirm.toString())) {
                binding.etNewPasswordConfirm.error = context.getString(R.string.password_validate)
                hasError = true
            } else if (newPassword.toString() != newPasswordConfirm.toString()) {
                binding.etNewPasswordConfirm.error =
                    context.getString(R.string.passwords_do_not_match)
                hasError = true
            }

            if (!hasError) {
                listener.onUpdate(oldPassword.toString(), newPassword.toString())
                dismiss()
            }
        }
    }
}

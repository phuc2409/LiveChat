package com.livechat.view.bottom_sheet

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.livechat.R
import com.livechat.databinding.BottomSheetChatBinding

/**
 * User: Quang Phúc
 * Date: 2023-04-26
 * Time: 11:11 PM
 */
class ChatBottomSheet(
    context: Context,
    private val listener: Listener
) : BottomSheetDialog(context, R.style.Theme_AlertDialog) {

    interface Listener {

        fun onSelectMedia()

        fun onSelectCamera()

        fun onSelectLocation()
    }

    private val binding = BottomSheetChatBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        initView()
        handleListener()
    }

    private fun initView() {

    }

    private fun handleListener() {
        binding.tvMedia.setOnClickListener {
            dismiss()
            listener.onSelectMedia()
        }

        binding.tvCamera.setOnClickListener {
            dismiss()
            listener.onSelectCamera()
        }

        binding.tvLocation.setOnClickListener {
            dismiss()
            listener.onSelectLocation()
        }
    }
}
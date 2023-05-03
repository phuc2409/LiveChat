package com.livechat.view.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.databinding.ItemMessageReceiveBinding
import com.livechat.databinding.ItemMessageSendBinding
import com.livechat.model.MessageModel

/**
 * User: Quang Phúc
 * Date: 2023-04-09
 * Time: 09:19 PM
 */
class MessageAdapter(
    private val context: Context,
    list: ArrayList<MessageModel>,
    private val listener: Listener
) : BaseAdapter<MessageModel>(list) {

    interface Listener {

        fun onAttachmentClick(attachmentModel: MessageModel.AttachmentModel, position: Int)
    }

    private class SendHolder(val binding: ItemMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ReceiveHolder(val binding: ItemMessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> SendHolder(ItemMessageSendBinding.inflate(li, parent, false))
            1 -> ReceiveHolder(ItemMessageReceiveBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is SendHolder -> {
                if (item.attachments.isEmpty()) {
                    holder.binding.tvMessage.text = item.message
                } else {
                    val spanCount = when (item.attachments.size) {
                        1 -> 1
                        2 -> 2
                        4 -> 2
                        else -> 3
                    }
                    val layoutManager = object : GridLayoutManager(context, spanCount) {

                        override fun isLayoutRTL(): Boolean {
                            return true
                        }
                    }
                    val adapter = AttachmentAdapter(
                        context,
                        item.attachments
                    ) { attachmentModel, attachmentPosition ->
                        listener.onAttachmentClick(attachmentModel, attachmentPosition)
                    }
                    holder.binding.rvAttachments.layoutManager = layoutManager
                    holder.binding.rvAttachments.adapter = adapter
                }
                item.createdAt?.let {
                    holder.binding.tvTime.text = it.toDate().toString()
                }
            }

            is ReceiveHolder -> {
                if (item.attachments.isEmpty()) {
                    holder.binding.tvMessage.text = item.message
                } else {
                    val spanCount = when (item.attachments.size) {
                        1 -> 1
                        2 -> 2
                        4 -> 2
                        else -> 3
                    }
                    val layoutManager = object : GridLayoutManager(context, spanCount) {

                    }
                    val adapter = AttachmentAdapter(
                        context,
                        item.attachments
                    ) { attachmentModel, attachmentPosition ->
                        listener.onAttachmentClick(attachmentModel, attachmentPosition)
                    }
                    holder.binding.rvAttachments.layoutManager = layoutManager
                    holder.binding.rvAttachments.adapter = adapter
                }
                item.createdAt?.let {
                    holder.binding.tvTime.text = it.toDate().toString()
                }
            }

            is ErrorHolder -> {

            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        if (Firebase.auth.currentUser == null) {
            return 2
        }
        return if (list[position].sendId == Firebase.auth.currentUser?.uid) {
            0
        } else {
            1
        }
    }
}
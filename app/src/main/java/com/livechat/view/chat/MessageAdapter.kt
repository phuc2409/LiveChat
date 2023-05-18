package com.livechat.view.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.common.CurrentUser
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.databinding.ItemMessageReceiveBinding
import com.livechat.databinding.ItemMessageSendBinding
import com.livechat.extension.gone
import com.livechat.extension.visible
import com.livechat.model.MessageModel
import com.livechat.model.MessageType
import com.livechat.util.TimeUtil

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

    var fullName: String = ""
    var avatarUrl: String = ""

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
                if (item.type == MessageType.INCOMING_VIDEO_CALL) {
                    item.createdAt?.let {
                        holder.binding.tvCallTime.text =
                            TimeUtil.formatTimestampToFullString(it.seconds)
                    }
                    holder.binding.tvCallStatus.text = context.getString(R.string.call)

                    holder.binding.llCall.visible()
                    holder.binding.tvTime.gone()
                    holder.binding.cvMessage.gone()
                    holder.binding.rvAttachments.gone()
                } else if (item.attachments.isEmpty()) {
                    item.createdAt?.let {
                        holder.binding.tvTime.text =
                            TimeUtil.formatTimestampToFullString(it.seconds)
                    }
                    holder.binding.tvMessage.text = item.message

                    holder.binding.llCall.gone()
                    holder.binding.tvTime.visible()
                    holder.binding.cvMessage.visible()
                    holder.binding.rvAttachments.gone()
                } else {
                    item.createdAt?.let {
                        holder.binding.tvTime.text =
                            TimeUtil.formatTimestampToFullString(it.seconds)
                    }

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

                    holder.binding.llCall.gone()
                    holder.binding.tvTime.visible()
                    holder.binding.cvMessage.gone()
                    holder.binding.rvAttachments.visible()
                }
            }

            is ReceiveHolder -> {
                if (item.type == MessageType.INCOMING_VIDEO_CALL) {
                    item.createdAt?.let {
                        holder.binding.tvCallTime.text =
                            TimeUtil.formatTimestampToFullString(it.seconds)
                    }
                    holder.binding.tvCallStatus.text =
                        "${context.getString(R.string.called_from)} $fullName"

                    holder.binding.llCall.visible()
                    holder.binding.imgAvatar.gone()
                    holder.binding.tvTime.gone()
                    holder.binding.cvMessage.gone()
                    holder.binding.rvAttachments.gone()
                } else if (item.attachments.isEmpty()) {
                    if (avatarUrl.isNotBlank()) {
                        Glide.with(context).load(avatarUrl).into(holder.binding.imgAvatar)
                    }
                    item.createdAt?.let {
                        holder.binding.tvTime.text =
                            TimeUtil.formatTimestampToFullString(it.seconds)
                    }
                    holder.binding.tvMessage.text = item.message

                    holder.binding.llCall.gone()
                    holder.binding.imgAvatar.visible()
                    holder.binding.tvTime.visible()
                    holder.binding.cvMessage.visible()
                    holder.binding.rvAttachments.gone()
                } else {
                    if (avatarUrl.isNotBlank()) {
                        Glide.with(context).load(avatarUrl).into(holder.binding.imgAvatar)
                    }
                    item.createdAt?.let {
                        holder.binding.tvTime.text =
                            TimeUtil.formatTimestampToFullString(it.seconds)
                    }

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

                    holder.binding.llCall.gone()
                    holder.binding.imgAvatar.visible()
                    holder.binding.tvTime.visible()
                    holder.binding.cvMessage.gone()
                    holder.binding.rvAttachments.visible()
                }
            }

            is ErrorHolder -> {

            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        if (CurrentUser.id.isBlank()) {
            return 2
        }
        return if (list[position].sendId == CurrentUser.id) {
            0
        } else {
            1
        }
    }
}

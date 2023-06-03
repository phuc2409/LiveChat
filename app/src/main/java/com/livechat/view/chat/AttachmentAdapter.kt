package com.livechat.view.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.common.Constants
import com.livechat.databinding.ItemAttachmentBinding
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.extension.gone
import com.livechat.extension.visible
import com.livechat.model.MessageModel
import com.livechat.util.TimeUtil

/**
 * User: Quang Phúc
 * Date: 2023-05-03
 * Time: 04:12 PM
 */
class AttachmentAdapter(
    private val context: Context,
    list: ArrayList<MessageModel.AttachmentModel>,
    private val listener: Listener
) : BaseAdapter<MessageModel.AttachmentModel>(list) {

    interface Listener {

        fun onClick(attachmentModel: MessageModel.AttachmentModel, attachmentPosition: Int)

        fun onLongClick(attachmentModel: MessageModel.AttachmentModel, attachmentPosition: Int)
    }

    private class ItemHolder(val binding: ItemAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemAttachmentBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is ItemHolder -> {
                Glide.with(context)
                    .load(item.url)
                    .placeholder(R.drawable.ic_android)
                    .centerCrop()
                    .into(holder.binding.imgThumbnail)

                if (item.type == Constants.IMAGE) {
                    holder.binding.llDuration.gone()
                    holder.binding.imgPlay.gone()
                } else {
                    holder.binding.llDuration.visible()
                    holder.binding.imgPlay.visible()
                    holder.binding.tvDuration.text = TimeUtil.formatMilliSecToHour(item.duration)
                }

                holder.itemView.setOnClickListener {
                    listener.onClick(item, position)
                }

                holder.itemView.setOnLongClickListener() {
                    listener.onLongClick(item, position)
                    true
                }
            }

            is ErrorHolder -> {

            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = 0
}
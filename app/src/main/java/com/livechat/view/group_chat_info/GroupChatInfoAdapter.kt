package com.livechat.view.group_chat_info

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.databinding.ItemContactBinding
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.model.ChatModel

/**
 * User: Quang Phúc
 * Date: 2023-06-19
 * Time: 03:21 AM
 */
class GroupChatInfoAdapter(
    private val context: Context,
    list: ArrayList<ChatModel.ParticipantModel>,
    private val listener: Listener
) : BaseAdapter<ChatModel.ParticipantModel>(list) {

    interface Listener {

        fun onClick(chatModel: ChatModel.ParticipantModel, position: Int)
    }

    private class ItemHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemContactBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is ItemHolder -> {
                holder.binding.tvName.text = item.name
                if (item.avatarUrl.isNotBlank()) {
                    Glide.with(context)
                        .load(item.avatarUrl)
                        .centerCrop()
                        .into(holder.binding.imgAvatar)
                }

                holder.itemView.setOnClickListener {
                    listener.onClick(item, position)
                }
            }

            is ErrorHolder -> {

            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = 0
}

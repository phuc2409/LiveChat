package com.livechat.view.create_group_chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.common.CurrentUser
import com.livechat.databinding.ItemContactSelectedBinding
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.extension.gone
import com.livechat.extension.visible
import com.livechat.model.ChatModel

/**
 * User: Quang Phúc
 * Date: 2023-06-19
 * Time: 01:25 AM
 */
class CreateGroupChatAdapter(
    private val context: Context,
    list: ArrayList<ChatModel>,
    private val listener: Listener
) : BaseAdapter<ChatModel>(list) {

    interface Listener {

        fun onClick(chatModel: ChatModel, position: Int)
    }

    private class ItemHolder(val binding: ItemContactSelectedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemContactSelectedBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is ItemHolder -> {
                for (i in item.participants) {
                    if (i.id != CurrentUser.id) {
                        holder.binding.tvName.text = i.name
                        if (i.avatarUrl.isBlank()) {
                            holder.binding.imgAvatar.setImageResource(R.drawable.ic_person)
                        } else {
                            Glide.with(context)
                                .load(i.avatarUrl)
                                .centerCrop()
                                .into(holder.binding.imgAvatar)
                        }
                        break
                    }
                }
                if (item.isSelected) {
                    holder.binding.imgCheck.visible()
                } else {
                    holder.binding.imgCheck.gone()
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

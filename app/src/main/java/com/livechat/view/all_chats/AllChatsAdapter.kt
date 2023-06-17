package com.livechat.view.all_chats

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.common.CurrentUser
import com.livechat.databinding.ItemAllChatsBinding
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.extension.gone
import com.livechat.extension.visible
import com.livechat.model.ChatModel
import com.livechat.util.TimeUtil

/**
 * User: Quang Phúc
 * Date: 2023-04-09
 * Time: 11:00 PM
 */
class AllChatsAdapter(
    private val context: Context,
    list: ArrayList<ChatModel>,
    private val onClick: (chatModel: ChatModel, position: Int) -> Unit
) : BaseAdapter<ChatModel>(list) {

    private class ItemHolder(val binding: ItemAllChatsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemAllChatsBinding.inflate(li, parent, false))
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
                        if (i.avatarUrl.isNotBlank()) {
                            Glide.with(context)
                                .load(i.avatarUrl)
                                .centerCrop()
                                .into(holder.binding.imgAvatar)
                        }
                        break
                    }
                }
                for (i in item.participants) {
                    if (i.id == CurrentUser.id) {
                        if (i.hasRead) {
                            holder.binding.tvName.typeface = Typeface.DEFAULT
                            holder.binding.imgUnread.gone()
                        } else {
                            holder.binding.tvName.typeface = Typeface.DEFAULT_BOLD
                            holder.binding.imgUnread.visible()
                        }
                        break
                    }
                }
                holder.binding.tvMessage.text = item.latestMessage
                item.updatedAt?.let {
                    holder.binding.tvTime.text = TimeUtil.formatTimestampToString(it.seconds)
                }

                holder.itemView.setOnClickListener {
                    onClick(item, position)
                }
            }

            is ErrorHolder -> {

            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = 0
}

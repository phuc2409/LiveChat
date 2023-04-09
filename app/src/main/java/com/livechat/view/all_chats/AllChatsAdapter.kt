package com.livechat.view.all_chats

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.databinding.ItemAllChatsBinding
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.model.ChatModel

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
                    if (i.id != Firebase.auth.currentUser?.uid) {
                        holder.binding.tvName.text = i.name
                        break
                    }
                }
                holder.binding.tvMessage.text = item.latestMessage
                holder.binding.tvTime.text = item.updatedAt?.toDate().toString()

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
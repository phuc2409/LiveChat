package com.livechat.view.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private val onClick: (messageModel: MessageModel, position: Int) -> Unit
) : BaseAdapter<MessageModel>(list) {

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
                holder.binding.tvMessage.text = item.message
                holder.binding.tvTime.text = item.createdAt?.toDate().toString()
            }

            is ReceiveHolder -> {
                holder.binding.tvMessage.text = item.message
                holder.binding.tvTime.text = item.createdAt?.toDate().toString()
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
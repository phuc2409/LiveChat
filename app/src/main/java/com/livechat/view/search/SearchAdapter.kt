package com.livechat.view.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.databinding.ItemSearchBinding
import com.livechat.extension.highlightEmTag
import com.livechat.model.UserModel

/**
 * User: Quang Phúc
 * Date: 2023-04-04
 * Time: 12:12 AM
 */
class SearchAdapter(
    private val context: Context,
    list: ArrayList<UserModel>,
    private val onClick: (userModel: UserModel, position: Int) -> Unit
) : BaseAdapter<UserModel>(list) {

    private class ItemHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemSearchBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is ItemHolder -> {
                if (item.avatarUrl.isNotBlank()) {
                    Glide.with(context)
                        .load(item.avatarUrl)
                        .centerCrop()
                        .into(holder.binding.imgAvatar)
                }

                holder.binding.tvFullName.text = item.fullName.highlightEmTag()
                holder.binding.tvUserName.text =
                    "${context.getString(R.string.user_name)}: ${item.userName}".highlightEmTag()

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

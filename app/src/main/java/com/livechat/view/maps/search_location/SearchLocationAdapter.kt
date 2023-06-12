package com.livechat.view.maps.search_location

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.databinding.ItemSearchLocationBinding
import com.livechat.model.api.TextSearchResponseModel

/**
 * User: Quang Phúc
 * Date: 2023-06-07
 * Time: 11:48 PM
 */
class SearchLocationAdapter(
    private val context: Context,
    list: ArrayList<TextSearchResponseModel.Result>,
    private val listener: Listener,
) : BaseAdapter<TextSearchResponseModel.Result>(list) {

    interface Listener {

        fun onClick(result: TextSearchResponseModel.Result, position: Int)

        fun onBindItem(position: Int)
    }

    private class ItemHolder(val binding: ItemSearchLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemSearchLocationBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is ItemHolder -> {
                Glide.with(context)
                    .load(item.icon)
                    .centerCrop()
                    .into(holder.binding.imgIcon)

                holder.binding.tvName.text = item.name
                holder.binding.tvAddress.text = item.formattedAddress

                holder.itemView.setOnClickListener {
                    listener.onClick(item, position)
                }
            }

            is ErrorHolder -> {

            }
        }

        listener.onBindItem(position)
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = 0
}

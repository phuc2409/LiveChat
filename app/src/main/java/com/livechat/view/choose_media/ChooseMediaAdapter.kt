package com.livechat.view.choose_media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseAdapter
import com.livechat.base.ErrorHolder
import com.livechat.databinding.ItemListErrorBinding
import com.livechat.databinding.ItemMediaBinding
import com.livechat.extension.gone
import com.livechat.extension.visible
import com.livechat.model.FileModel
import com.livechat.util.TimeUtil

/**
 * User: Quang Phúc
 * Date: 2023-05-01
 * Time: 10:12 PM
 */
class ChooseMediaAdapter(
    private val context: Context,
    list: ArrayList<FileModel>,
    private val onClick: (fileModel: FileModel, position: Int) -> Unit
) : BaseAdapter<FileModel>(list) {

    private class ItemHolder(val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = LayoutInflater.from(context)
        return when (viewType) {
            0 -> ItemHolder(ItemMediaBinding.inflate(li, parent, false))
            else -> ErrorHolder(ItemListErrorBinding.inflate(li, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {

            is ItemHolder -> {
                Glide.with(context)
                    .load(item.path)
                    .placeholder(R.drawable.ic_android)
                    .centerCrop()
                    .into(holder.binding.imgThumbnail)
                if (item.type == FileModel.Type.VIDEO) {
                    holder.binding.llDuration.visible()
                    holder.binding.tvDuration.text = TimeUtil.formatMilliSecToHour(item.duration)
                } else {
                    holder.binding.llDuration.gone()
                }
                if (item.isSelected) {
                    holder.binding.llSelect.visible()
                } else {
                    holder.binding.llSelect.gone()
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
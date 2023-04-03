package com.livechat.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.livechat.R

/**
 * User: Quang Phúc
 * Date: 2023-04-04
 * Time: 12:14 AM
 */
abstract class BaseAdapter<T>(val list: ArrayList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val viewTypeTop = 1
    val viewTypeBody = 2
    val viewTypeFooter = 3
    val viewTypeMore = 0
    val viewTypeError = -1

    var page = 1
    var pageFixed = -1
    private var currentPage = 0
    var msg: String? = ""
    var title: String? = ""
    var textButton: String? = ""
    var iconError: Int = R.drawable.ic_chat
    var previousPosition = -1
    var isLoading = false
    var fullData = false

    fun getItemByPosition(position: Int) = if (position < list.size) list[position] else null

    var itemClickListener: ((position: Int) -> Unit)? = null
    var tryAgainClickListener: (() -> Unit)? = null

    private var morelistener: ((Int) -> Unit?)? = null

    fun setLoadMore(recyclerView: RecyclerView, listener: (page: Int) -> Unit) {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        var lastVisibleItem: Int
        var totalItemCount: Int
        val visibleThreshold = 10
        morelistener = listener

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (fullData) return
                totalItemCount = linearLayoutManager.itemCount
                if (totalItemCount == 0) return
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (currentPage != page) {
                        morelistener?.invoke(page)
                        isLoading = true
                        currentPage = page
                    }
                }
            }
        })
    }

    /**
     * reset lại list
     */
    fun resetLoadMore() {
        fullData = false
        isLoading = false
        page = 1
        currentPage = 0
    }

    /**
     * Trường hợp load dữ liệu hoàn thành mà dữ liệu vẫn còn
     */
    private fun loadDataSuccess() {
        fullData = false
        isLoading = false
        page += 1
    }

    /**
     * Trường hợp load dữ liệu hoàn thành mà dữ liệu đã hết
     */
    private fun setFullData() {
        isLoading = false
        fullData = true
    }

    fun addData(reset: Boolean, newData: List<T>) {
        if (reset) list.clear()
        msg = null

        val oldSize = list.size
        list.addAll(newData)

        if (newData.size < 20) {
            setFullData()
        } else {
            loadDataSuccess()
        }
        if (reset) {
            notifyDataSetChanged()
        } else
            notifyItemRangeChanged(oldSize, list.size + 1, Unit)
    }

    /**
     * Chỉ xoá item khỏi giao diện
     */
    fun removeItemFromView(position: Int) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount, Unit)
    }

    /**
     * Xoá item khỏi giao diện và xoá cả item trong list
     */
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    /**
     * Chỉ xoá tất cả item khỏi giao diện
     */
    fun removeAllItemsFromView() {
        notifyItemRangeRemoved(0, itemCount)
//        notifyItemRangeChanged(position, itemCount, Unit)
    }

    /**
     * Xoá tất cả item khỏi giao diện và xoá cả item trong list
     */
    fun removeAll() {
        list.clear()
        notifyDataSetChanged()
    }

    fun setError(title: String?, msg: String?) {
        this.msg = msg
        this.title = title
        list.clear()
        notifyDataSetChanged()
    }

    fun setError(textButton: String?, title: String?, msg: String?) {
        this.msg = msg
        this.textButton = textButton
        this.title = title
        list.clear()
        notifyDataSetChanged()
    }

    fun setError(iconError: Int, textButton: String?, title: String?, msg: String?) {
        this.msg = msg
        this.textButton = textButton
        this.title = title
        this.iconError = iconError
        list.clear()
        notifyDataSetChanged()
    }
}

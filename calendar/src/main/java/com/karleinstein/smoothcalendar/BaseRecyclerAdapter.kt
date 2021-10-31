package com.karleinstein.smoothcalendar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

abstract class BaseRecyclerAdapter<Item>(
    callBack: DiffUtil.ItemCallback<Item>,
    private vararg val onClickItem: (item: Item) -> Unit
) : ListAdapter<Item, BaseRecyclerAdapter<Item>.BaseViewHolder>(
    AsyncDifferConfig.Builder<Item>(callBack)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {

    abstract fun getLayoutRes(viewType: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(getLayoutRes(viewType), parent, false)
        )
            .apply {
                bindFirstTime(this)
            }
    }

    protected open fun bindFirstTime(baseViewHolder: BaseViewHolder) {}

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        onBind(holder.itemView, item, holder.adapterPosition)
    }

    abstract fun onBind(itemView: View, item: Item, position: Int)

    open inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

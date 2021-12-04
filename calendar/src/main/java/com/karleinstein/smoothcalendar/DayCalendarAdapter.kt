package com.karleinstein.smoothcalendar

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import com.karleinstein.smoothcalendar.utils.isSameMonth
import com.karleinstein.smoothcalendar.utils.isToday
import kotlinx.android.synthetic.main.item_layout_days_2.view.*
import org.threeten.bp.Month

internal class DayCalendarAdapter(
    private val month: Month,
    onClickItem: (item: DateWrapper) -> Unit = {},
    private val onDateClickListener: (dateWrapper: DateWrapper) -> Unit
) : BaseRecyclerAdapter<DateWrapper>(object : DiffUtil.ItemCallback<DateWrapper>() {
    //force trigger
    override fun areItemsTheSame(
        oldItem: DateWrapper,
        newItem: DateWrapper
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: DateWrapper,
        newItem: DateWrapper
    ): Boolean {
        return oldItem==newItem
    }

}, onClickItem) {

    private var selectedPosition: Int = -1

    override fun getLayoutRes(viewType: Int): Int = if (viewType == 3) {
        R.layout.item_layout_days_collapse
    } else R.layout.item_layout_days_2

    override fun onBind(itemView: View, item: DateWrapper, position: Int) {
        with(itemView) {
            text_day_2.text = item.date.dayOfMonth.toString()
            if (selectedPosition == position) {
                onDateClickListener(item)
            }
            text_day_2.setColorClicked(selectedPosition == position)
            if (item.date.isSameMonth(month.value)) {
                text_day_2.setTextColor(Color.parseColor("#808080"))
            }
            if (item.date.isToday()) {
                text_day_2.typeface = ResourcesCompat.getFont(context, R.font.proximanova_bold)
                text_day_2.setTextColor(
                    resources.getColor(
                        android.R.color.holo_red_dark,
                        resources.newTheme()
                    )
                )
            }
            when (item.stateMarked) {
                StateMarked.ONLY_MARKED -> {
                    text_day_2.setBackgroundResource(R.drawable.bg_selected_day_2)
                }
                StateMarked.STARTED_MARKED -> {
                    view_bg_start.setBackgroundResource(R.drawable.bg_marked)
                    text_day_2.setBackgroundResource(R.drawable.bg_selected_day_2)
                }
                StateMarked.END_MARKED->{
                    view_bg_end.setBackgroundResource(R.drawable.bg_marked)
                    text_day_2.setBackgroundResource(R.drawable.bg_selected_day_2)
                }
                StateMarked.MARKED->{
                    parent_days_2.setBackgroundResource(R.drawable.bg_marked)
                }
                else -> {}
            }
        }
    }

    override fun bindFirstTime(baseViewHolder: BaseViewHolder) {
        super.bindFirstTime(baseViewHolder)
        baseViewHolder.itemView.setOnClickListener {
            selectedPosition = baseViewHolder.adapterPosition
            submitList(currentList)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isCollapsed) {
            3
        } else
            1
    }


    private fun View.setColorClicked(isClicked: Boolean = true) {
        if (isClicked) {
            this.setBackgroundResource(R.drawable.bg_selected_day_2)
        } else {
            this.setBackgroundResource(R.drawable.bg_unselected_day)
        }
    }

    private fun TextView.setDefaultColor() {
        setTextColor(this.textColors)
    }
}

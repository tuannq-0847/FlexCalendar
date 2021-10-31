package com.karleinstein.smoothcalendar

import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import com.karleinstein.smoothcalendar.utils.isToday
import kotlinx.android.synthetic.main.item_layout_days.view.*

internal class DayCalendarAdapter(
    onClickItem: (item: MonthWrapper) -> Unit = {},
    private val onDateClickListener: (monthWrapper: MonthWrapper) -> Unit
) : BaseRecyclerAdapter<MonthWrapper>(object : DiffUtil.ItemCallback<MonthWrapper>() {
    //force trigger
    override fun areItemsTheSame(
        oldItem: MonthWrapper,
        newItem: MonthWrapper
    ): Boolean {
        return true
    }

    override fun areContentsTheSame(
        oldItem: MonthWrapper,
        newItem: MonthWrapper
    ): Boolean {
        return false
    }

}, onClickItem) {

    private var selectedPosition: Int = -1

    override fun getLayoutRes(viewType: Int): Int = if (viewType == 3) {
        R.layout.item_layout_days_collapse
    } else R.layout.item_layout_days

    override fun onBind(itemView: View, item: MonthWrapper, position: Int) {
        with(itemView) {
            text_day.text = item.months.dayOfMonth.toString()
            if (selectedPosition == position) {
                onDateClickListener(item)
            }

            parent_days.setColorClicked(selectedPosition == position)
            if (item.months.isToday()) {
                text_day.typeface = ResourcesCompat.getFont(context, R.font.proximanova_bold)
                text_day.setTextColor(
                    resources.getColor(
                        android.R.color.black,
                        resources.newTheme()
                    )
                )
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
            this.setBackgroundColor(
                resources.getColor(
                    R.color.colorRedBB,
                    resources.newTheme()
                )
            )
            this.setBackgroundResource(R.drawable.bg_selected_day)
        } else {
            this.setBackgroundColor(
                resources.getColor(
                    android.R.color.black,
                    resources.newTheme()
                )
            )
            this.setBackgroundResource(R.drawable.bg_unselected_day)
        }
    }

    private fun TextView.setDefaultColor() {
        setTextColor(this.textColors)
    }
}

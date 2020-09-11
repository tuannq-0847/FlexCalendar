package com.karleinstein.smoothcalendar

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karleinstein.smoothcalendar.listener.OnSwipeListener
import kotlinx.android.synthetic.main.winner_calendar_layout.view.*
import org.threeten.bp.LocalDate

class CustomCalendarAdapter(
    val onSwipe: (swipeDirection: SwipeDirection, item: List<MonthWrapper>) -> Unit,
    private val onDateClickListener: (monthWrapper: MonthWrapper) -> Unit
) : BaseRecyclerAdapter<List<MonthWrapper>>(object :
    DiffUtil.ItemCallback<List<MonthWrapper>>() {
    override fun areItemsTheSame(
        oldItem: List<MonthWrapper>,
        newItem: List<MonthWrapper>
    ): Boolean {
        return oldItem[0].months == newItem[0].months
    }

    override fun areContentsTheSame(
        oldItem: List<MonthWrapper>,
        newItem: List<MonthWrapper>
    ): Boolean {
        return oldItem == newItem
    }

}), View.OnTouchListener {

    override fun getLayoutRes(viewType: Int): Int = R.layout.winner_calendar_layout
    private var isCollapsed = true
    private lateinit var gestureDetector: GestureDetector
    var isEnableScrolled = true

    override fun onBind(
        itemView: View, item: List<MonthWrapper>, position: Int
    ) {
        itemView.run {
            val dayCalendarAdapter =
                DayCalendarAdapter(onDateClickListener = onDateClickListener)

            val layoutManager = object : GridLayoutManager(context, 7) {
                override fun canScrollVertically(): Boolean {
                    return isEnableScrolled
                }
            }
            recycler_inside.setOnTouchListener(this@CustomCalendarAdapter)
            recycler_inside.setHasFixedSize(true)
            recycler_inside.layoutManager = layoutManager
            recycler_inside.adapter = dayCalendarAdapter
            isCollapsed = true
            gestureDetector = GestureDetector(context, object : OnSwipeListener() {
                override fun onSwipe(direction: Direction?): Boolean {
                    Log.d("onSwipeSmooth", "onSwipe: $direction")
                    when (direction) {
                        Direction.up -> {
                            onSwipe(SwipeDirection.UP, item)
                        }
                        Direction.down -> {
                            onSwipe(SwipeDirection.DOWN, item)
                        }
                        Direction.left -> {
                            onSwipe(SwipeDirection.LEFT, item)
                        }
                        Direction.right -> {
                            onSwipe(SwipeDirection.RIGHT, item)
                        }
                    }
                    return false
                }
            })
            dayCalendarAdapter.submitList(item.toMutableList())
            recycler_inside.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    LogUtils.d("scroll recyclerview: $dx $dy ${recyclerView.height} ${recyclerView.width} ${recyclerView.x} ${recyclerView.y}")
//                    val state = isNeedCollapse(dy, isCollapsed)
//                    state?.let {
//                    if (state) collapse(a, item) else expand(a, item)
//                    }
                }
            })
        }
    }

    private fun expand(adapter: DayCalendarAdapter, item: List<LocalDate>) {
        val monthWrappers = mutableListOf<MonthWrapper>()
        item.forEach {
            monthWrappers.add(MonthWrapper(it, isCollapsed = false))
        }
        adapter.submitList(null)
        adapter.submitList(monthWrappers)
        isCollapsed = true
    }

    private fun collapse(
        adapter: DayCalendarAdapter,
        item: List<LocalDate>
    ) {
        val monthWrappers = mutableListOf<MonthWrapper>()
        item.forEach {
            monthWrappers.add(MonthWrapper(it, isCollapsed = true))
        }
        adapter.submitList(null)
        adapter.submitList(monthWrappers)
        isCollapsed = false
    }

    private fun isNeedCollapse(dy: Int, isCollapsed: Boolean): Boolean? =
        if (isCollapsed && dy > 0) {
            true
        } else if (!isCollapsed && dy < 0) {
            false
        } else null

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return false
    }
}

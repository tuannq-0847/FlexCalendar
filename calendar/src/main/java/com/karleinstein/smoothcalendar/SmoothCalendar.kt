package com.karleinstein.smoothcalendar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.karleinstein.smoothcalendar.config.MonthConfig
import com.karleinstein.smoothcalendar.config.changeConfig
import com.karleinstein.smoothcalendar.utils.CalendarListener
import com.karleinstein.smoothcalendar.utils.SmoothCalendarListener
import com.karleinstein.smoothcalendar.utils.getSnapPosition
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.YearMonth


class SmoothCalendar(context: Context, attrs: AttributeSet?) :
    RecyclerView(context, attrs),
    SmoothCalendarListener, View.OnTouchListener {

    private var snapPosition = NO_POSITION
    private var calendarListener: CalendarListener? = null
    private val snapHelper = PagerSnapHelper()
    private var boundedMonths = mutableListOf<MonthWrapper>()
    var startMonth: YearMonth? = null
    var endMonth: YearMonth? = null
    var isEnableScroll = true

    private lateinit var customAdapter: CustomCalendarAdapter

    override fun setOnCalendarListener(calendarListener: CalendarListener) {
        this.calendarListener = calendarListener
    }

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
//        setOnTouchListener(this)
    }

    private fun groupConsecutiveDay(
        dateHistory: List<DateWrapper>,
    ): MutableMap<String, MutableList<DateWrapper>> {
        val new = dateHistory.sortedBy { it.date.dayOfYear }
        val mp = mutableMapOf<String, MutableList<DateWrapper>>()
        var count = 0
        var key = "Calendar: $count"
        mp[key] = mutableListOf(new[0])
        for (i in 1 until new.size) {
            val last = new[i - 1]
            if (new[i].date.dayOfYear - last.date.dayOfYear == 1) {
                if (mp.containsKey(key))
                    mp[key] = mp[key]!!.apply {
                        add(new[i])
                    }
                else
                    mp[key] = mutableListOf(new[i])
            } else {
                count++
                key = "Calendar: $count"
                mp[key] = mutableListOf(new[i])
            }
        }
        return mp
    }


    fun setTime(dateHistory: List<DateWrapper>) {
        if (dateHistory.isEmpty())
            return
        val mp = groupConsecutiveDay(dateHistory)
        val dateWrapperSet = mutableSetOf<DateWrapper>()
        mp.forEach {
            if (it.value.size == 1) {
                it.value.first().stateMarked = StateMarked.ONLY_MARKED
                dateWrapperSet.add(it.value.first())
            } else {
                for (i in 0 until it.value.size) {
                    when (i) {
                        0 -> it.value[i].stateMarked = StateMarked.STARTED_MARKED
                        it.value.size - 1 -> it.value[i].stateMarked =
                            StateMarked.END_MARKED
                        else -> it.value[i].stateMarked = StateMarked.MARKED
                    }
                    dateWrapperSet.add(it.value[i])
                }
            }
        }

        val data = customAdapter.currentList.map { month ->
            month.dates = month.dates.map { date ->
                val d = dateWrapperSet.find {
                    it.date == date.date
                }
                d?.apply {
                    this.date = date.date
                    this.event = date.event
                    this.isCollapsed = date.isCollapsed
                } ?: date
            }
            month
        }
        customAdapter.submitList(data)
    }

    override fun scrollToMonth(month: Month) {
        customAdapter.currentList.forEachIndexed { index, list ->
            if (list.dates[10].date.month == month) {
                scrollToPosition(index)
            }
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        notifySnapPositionChanged(this)
    }

    private fun notifySnapPositionChanged(smoothCalendar: SmoothCalendar) {
        val snapPosition = snapHelper.getSnapPosition(smoothCalendar)
        val isSnapPositionChanged = this.snapPosition != snapPosition
        if (isSnapPositionChanged) {
            calendarListener?.onSnapPositionChange(boundedMonths[snapPosition], snapPosition)
            this.snapPosition = snapPosition
        }
    }

    private val getMonth = fun(month: Month) {
        Log.d("TAG", "getMonth: ${month.name}")
        calendarListener?.getMonth(month)
    }

    fun setup(startMonth: YearMonth, endMonth: YearMonth, firstDayOfWeek: DayOfWeek) {
        this.startMonth = startMonth
        this.endMonth = endMonth
        boundedMonths = MonthConfig().generateBoundedMonths(startMonth, endMonth, firstDayOfWeek)
        customAdapter = CustomCalendarAdapter(swipeListener, getMonth, onDateListener)
        layoutManager =
            object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    customAdapter.isEnableScrolled = this@SmoothCalendar.isEnableScroll
                    return isEnableScroll
                }
            }
//        addItemDecoration(HeaderItemDecoration(this, false) {
//            this.adapter?.getItemViewType(it) == R.layout.layout_month
//        })
        adapter = customAdapter
        //set page when scroll recyclerview each item
        snapHelper.attachToRecyclerView(this)
        customAdapter.submitList(boundedMonths)
        scrollToMonth(LocalDate.now().month)
    }

    private val onDateListener = fun(dateWrapper: DateWrapper) {
        calendarListener?.onClickDateListener(dateWrapper)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        calendarListener = null
    }

    private val swipeListener = fun(swipeDirection: SwipeDirection, item: MonthWrapper) {
        Log.d("TAG", ": $swipeDirection")
        when (swipeDirection) {
            SwipeDirection.RIGHT -> {

            }
            SwipeDirection.UP -> {
                customAdapter.submitList(null)
                customAdapter.submitList(boundedMonths.changeConfig(true, item.dates))
            }
            SwipeDirection.LEFT -> {

            }
            SwipeDirection.DOWN -> {
                customAdapter.submitList(null)
                customAdapter.submitList(boundedMonths.changeConfig(false, item.dates))
            }
        }
        calendarListener?.onSwipe(swipeDirection, item)
    }

    fun setCalendarEvent(events: List<DateWrapper>) {
        val eventsWithoutDuplicate =
            events.groupBy { it.date }.map {
                it.value.first().event = mergeEvent(it.value)
                return@map it
            }.map {
                return@map it.value.first()
            }
        boundedMonths.forEach { monthWrappers ->
            monthWrappers.dates.forEach { monthWrapper ->
                eventsWithoutDuplicate.forEach {
                    if (monthWrapper.date == it.date && monthWrapper.event.isEmpty()) {
                        monthWrapper.event = it.event
                    }
                }
            }
        }

        customAdapter.submitList(boundedMonths)
    }

    private fun mergeEvent(events: List<DateWrapper>): String {
        var data = ""
        events.forEach {
            data += it.event + "\n"
        }
        return data
    }

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when (motionEvent?.action) {
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(
                    "KARLEINSTEIN",
                    "onMotionEvent: ${motionEvent?.action} rawX: ${motionEvent?.rawX} rawY: ${motionEvent?.rawY}"
                )
            }
        }
        return true
    }
}

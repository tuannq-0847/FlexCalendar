package com.karleinstein.smoothcalendar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.DiffUtil
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
    private var boundedMonths = mutableListOf<List<MonthWrapper>>()
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

    override fun scrollToMonth(month: Month) {
        customAdapter.currentList.forEachIndexed { index, list ->
            if (list[10].months.month == month) {
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
            calendarListener?.onSnapPositionChange(boundedMonths[snapPosition].toMutableList())
            this.snapPosition = snapPosition
        }
    }

    fun setup(startMonth: YearMonth, endMonth: YearMonth, firstDayOfWeek: DayOfWeek) {
        this.startMonth = startMonth
        this.endMonth = endMonth
        boundedMonths = MonthConfig().generateBoundedMonths(startMonth, endMonth, firstDayOfWeek)
        customAdapter = CustomCalendarAdapter(swipeListener, onDateListener)
        layoutManager =
            object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    customAdapter.isEnableScrolled = this@SmoothCalendar.isEnableScroll
                    return isEnableScroll
                }
            }
        adapter = customAdapter
        //set page when scroll recyclerview each item
        snapHelper.attachToRecyclerView(this)
        customAdapter.submitList(boundedMonths)
        scrollToMonth(LocalDate.now().month)
    }

    private val onDateListener = fun(monthWrapper: MonthWrapper) {
        calendarListener?.onClickDateListener(monthWrapper)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        calendarListener = null
    }

    private val swipeListener = fun(swipeDirection: SwipeDirection, item: List<MonthWrapper>) {
        Log.d("TAG", ": $swipeDirection")
        when (swipeDirection) {
            SwipeDirection.RIGHT -> {

            }
            SwipeDirection.UP -> {
                customAdapter.submitList(null)
                customAdapter.submitList(boundedMonths.changeConfig(true, item))
            }
            SwipeDirection.LEFT -> {

            }
            SwipeDirection.DOWN -> {
                customAdapter.submitList(null)
                customAdapter.submitList(boundedMonths.changeConfig(false, item))
            }
        }
        calendarListener?.onSwipe(swipeDirection)
    }

    fun setCalendarEvent(events: List<MonthWrapper>) {
        val eventsWithoutDuplicate =
            events.groupBy { it.months }.map {
                it.value.first().event = mergeEvent(it.value)
                return@map it
            }.map {
                return@map it.value.first()
            }
        boundedMonths.forEach { monthWrappers ->
            monthWrappers.forEach { monthWrapper ->
                eventsWithoutDuplicate.forEach {
                    if (monthWrapper.months == it.months && monthWrapper.event.isEmpty()) {
                        monthWrapper.event = it.event
                    }
                }
            }
        }

        customAdapter.submitList(boundedMonths)
    }

    private fun mergeEvent(events: List<MonthWrapper>): String {
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
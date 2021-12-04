package com.karleinstein.smoothcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.karleinstein.smoothcalendar.utils.CalendarListener
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.YearMonth

class MainSmoothCalendar(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs),
    CalendarListener {

    private var smoothCalendar: SmoothCalendar? = null

    private var view: View? = null

    init {
        orientation = VERTICAL
        addCalendarView()
        smoothCalendar?.setOnCalendarListener(this)
    }

    private fun addCalendarView() {
        view = LayoutInflater.from(context).inflate(R.layout.layout_month, this)
        LayoutInflater.from(context).inflate(R.layout.item_sticky_header2, this)
        smoothCalendar = SmoothCalendar(context, null)
        smoothCalendar?.isEnableScroll = true
        addView(smoothCalendar)
        val currentMonth = YearMonth.now()
        val daysOfWeek = DayOfWeek.values()
        smoothCalendar?.setup(
            currentMonth.minusMonths(5),
            currentMonth.plusMonths(5),
            daysOfWeek.last()
        )
    }

    fun setOnCalendarListener(calendarListener: CalendarListener) {
//        smoothCalendar?.setOnCalendarListener(calendarListener)
    }

    fun setTime(dateHistory: List<DateWrapper>) = smoothCalendar?.setTime(dateHistory)

    override fun getMonth(month: Month) {

    }

    override fun onSnapPositionChange(data: MonthWrapper) {
        val result = "${
            data.month.month.name.lowercase().replaceFirstChar { it.uppercase() }
        } ${data.month.year}"
        view?.findViewById<TextView>(R.id.text_month)?.text = result
    }

    override fun onSwipe(direction: SwipeDirection, item: MonthWrapper) {

    }

    override fun onClickDateListener(dateWrapper: DateWrapper) {

    }
}

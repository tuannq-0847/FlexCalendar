package com.karleinstein.smoothcalendar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.karleinstein.smoothcalendar.utils.CalendarListener
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth

class MainSmoothCalendar(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var smoothCalendar: SmoothCalendar? = null

    init {
        orientation = VERTICAL
        addCalendarView()
    }

    private fun addCalendarView() {
        LayoutInflater.from(context).inflate(R.layout.item_sticky_header, this)
        smoothCalendar = SmoothCalendar(context, null)
        smoothCalendar?.isEnableScroll = true
        addView(smoothCalendar)
        val currentMonth = YearMonth.now()
        val daysOfWeek = DayOfWeek.values()
        Log.d("Fuck", "-5: ${currentMonth.minusMonths(5)} +5: ${currentMonth.plusMonths(5)} ")
        smoothCalendar?.setup(
            currentMonth.minusMonths(5),
            currentMonth.plusMonths(5),
            daysOfWeek.last()
        )
    }

    fun setOnCalendarListener(calendarListener: CalendarListener) {
        smoothCalendar?.setOnCalendarListener(calendarListener)
    }
}

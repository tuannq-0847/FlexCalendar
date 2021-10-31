package com.karleinstein.smoothcalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.karleinstein.smoothcalendar.config.MonthConfig
import com.karleinstein.smoothcalendar.utils.CalendarListener
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth

class MainActivity : AppCompatActivity(), CalendarListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smooth_calendar.setOnCalendarListener(this)
    }

    override fun getMonth(month: Int) {

    }

    override fun onSnapPositionChange(data: MutableList<MonthWrapper>) {

    }

    override fun onSwipe(direction: SwipeDirection) {

    }

    override fun onClickDateListener(monthWrapper: MonthWrapper) {
        Log.d("KARLEINSTEIN", "onClickDateListener: ${monthWrapper.months}")
    }
}

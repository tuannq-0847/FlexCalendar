package com.karleinstein.smoothcalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.karleinstein.smoothcalendar.utils.CalendarListener
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.Month

class MainActivity : AppCompatActivity(), CalendarListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smooth_calendar.setOnCalendarListener(this)
    }

    override fun getMonth(month: Month) {

    }

    override fun onSnapPositionChange(data: MonthWrapper, snapPosition: Int) {

    }

    override fun onSwipe(direction: SwipeDirection, item: MonthWrapper) {

    }

    override fun onClickDateListener(dateWrapper: DateWrapper) {
        Log.d("KARLEINSTEIN", "onClickDateListener: ${dateWrapper.date}")
    }
}

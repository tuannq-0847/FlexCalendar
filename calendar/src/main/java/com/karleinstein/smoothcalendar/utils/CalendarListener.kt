package com.karleinstein.smoothcalendar.utils

import com.karleinstein.smoothcalendar.DateWrapper
import com.karleinstein.smoothcalendar.MonthWrapper
import com.karleinstein.smoothcalendar.SwipeDirection
import org.threeten.bp.Month

interface CalendarListener {

    fun getMonth(month: Month)

    fun onSnapPositionChange(data: MonthWrapper)

    fun onSwipe(direction: SwipeDirection, item: MonthWrapper)

    fun onClickDateListener(dateWrapper: DateWrapper)
}

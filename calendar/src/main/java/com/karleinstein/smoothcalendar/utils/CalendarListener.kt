package com.karleinstein.smoothcalendar.utils

import com.karleinstein.smoothcalendar.MonthWrapper
import com.karleinstein.smoothcalendar.SwipeDirection

interface CalendarListener {

    fun getMonth(month: Int)

    fun onSnapPositionChange(data: MutableList<MonthWrapper>)

    fun onSwipe(direction: SwipeDirection)

    fun onClickDateListener(monthWrapper: MonthWrapper)
}

package com.karleinstein.smoothcalendar.utils

import org.threeten.bp.Month

interface SmoothCalendarListener {
    fun setOnCalendarListener(calendarListener: CalendarListener)

    fun scrollToMonth(month: Month)
}
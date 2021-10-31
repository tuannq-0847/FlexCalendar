package com.karleinstein.smoothcalendar

import org.threeten.bp.LocalDate

data class MonthWrapper(
    var months: LocalDate,
    var event: String = "",
    var isCollapsed: Boolean = true
)

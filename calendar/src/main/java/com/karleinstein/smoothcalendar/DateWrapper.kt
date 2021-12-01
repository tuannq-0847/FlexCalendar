package com.karleinstein.smoothcalendar

import org.threeten.bp.LocalDate
import org.threeten.bp.Month

data class DateWrapper(
    var date: LocalDate,
    var event: String = "",
    var stateMarked: StateMarked = StateMarked.NONE,
    var isCollapsed: Boolean = true
)

enum class StateMarked {
    MARKED_BOLD,
    MARKED,
    NONE
}

data class MonthWrapper(
    val month: MonthYear,
    var dates: List<DateWrapper>
)

data class MonthYear(
    val month: Month,
    val year: Int
)

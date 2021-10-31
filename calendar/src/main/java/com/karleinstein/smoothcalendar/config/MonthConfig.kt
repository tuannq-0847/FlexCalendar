package com.karleinstein.smoothcalendar.config

import com.karleinstein.smoothcalendar.MonthWrapper
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields

internal class MonthConfig {
    private val months = mutableListOf<List<LocalDate>>()

    //startMonth: first month when generate months
    //endMonth: end month when generate months
    //firstDayOfWeek: First day of the weeks like Monday....
    fun generateBoundedMonths(
        startMonth: YearMonth,
        endMonth: YearMonth,
        firstDayOfWeek: DayOfWeek
    ): MutableList<List<MonthWrapper>> {
        var currentMonth = startMonth
        while (currentMonth <= endMonth) {
            val localDate = generateBoundedDays(currentMonth, firstDayOfWeek)
            months.add(localDate)
            currentMonth = currentMonth.plusMonths(1)
//            if(currentMonth != endMonth) currentMonth = currentMonth.plusMonths(1) else break
        }
        return months.changeAllConfig(false)
    }


    //generate days in each months
    private fun generateBoundedDays(
        yearMonth: YearMonth,
        firstDayOfWeek: DayOfWeek
    ): List<LocalDate> {
        val localDates = mutableListOf<LocalDate>()
        val year = yearMonth.year
        val month = yearMonth.monthValue
        (1..yearMonth.lengthOfMonth()).map {
            val localDate = LocalDate.of(year, month, it)
            localDates.add(localDate)
        }
        val weekOfMonthField = WeekFields.of(firstDayOfWeek, 1).weekOfMonth()
        val groupByWeekOfMonth =
            localDates.groupBy { it.get(weekOfMonthField) }.values.toMutableList()
        val firstWeek = groupByWeekOfMonth.first()
        if (firstWeek.size < 7) {
            val previousMonth = yearMonth.minusMonths(1)
            val inDates = (1..previousMonth.lengthOfMonth()).toList()
                .takeLast(7 - firstWeek.size).map {
                    LocalDate.of(previousMonth.year, previousMonth.month, it)
                }
            groupByWeekOfMonth[0] = inDates + firstWeek
        }
        val lastWeek = groupByWeekOfMonth.last()
        if (lastWeek.size < 7) {
            val nextMonth = yearMonth.plusMonths(1)
            val inDates = (1..nextMonth.lengthOfMonth()).toList()
                .take(7 - lastWeek.size).map {
                    LocalDate.of(nextMonth.year, nextMonth.month, it)
                }
            groupByWeekOfMonth[groupByWeekOfMonth.size - 1] = lastWeek + inDates
        }

        val a = groupByWeekOfMonth.toList().flatMap {
            localDates.clear()
            localDates.addAll(it)
            localDates
        }
        return a
    }
}


fun MutableList<List<LocalDate>>.changeAllConfig(
    isCollapse: Boolean
): MutableList<List<MonthWrapper>> {
    val boundedMonthWrappers = mutableListOf<List<MonthWrapper>>()
    forEach {
        val monthWrappers = mutableListOf<MonthWrapper>()
        it.forEach {
            monthWrappers.add(MonthWrapper(it, isCollapsed = isCollapse))
        }
        boundedMonthWrappers.add(monthWrappers)
    }
    return boundedMonthWrappers
}

fun MutableList<List<MonthWrapper>>.changeConfig(
    isCollapse: Boolean,
    item: List<MonthWrapper>
): MutableList<List<MonthWrapper>> {
    this.filter { it == item }.firstOrNull().also {
        it?.let {
            val newMonthWrapper: MutableList<MonthWrapper> = mutableListOf()
            it.forEach {
                newMonthWrapper.add(MonthWrapper(it.months, isCollapsed = isCollapse))
            }
            this.mapIndexed { index, list ->
                if (list == item) {
                    this[index] = newMonthWrapper
                }
            }
        } ?: return this
    }
    return this
}

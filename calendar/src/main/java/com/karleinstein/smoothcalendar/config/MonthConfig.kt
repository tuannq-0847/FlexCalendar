package com.karleinstein.smoothcalendar.config

import com.karleinstein.smoothcalendar.DateWrapper
import com.karleinstein.smoothcalendar.MonthWrapper
import com.karleinstein.smoothcalendar.MonthYear
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import kotlin.reflect.full.memberProperties

internal class MonthConfig {
    private val months = mutableListOf<List<LocalDate>>()

    //startMonth: first month when generate months
    //endMonth: end month when generate months
    //firstDayOfWeek: First day of the weeks like Monday....
    fun generateBoundedMonths(
        startMonth: YearMonth,
        endMonth: YearMonth,
        firstDayOfWeek: DayOfWeek
    ): MutableList<MonthWrapper> {
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
): MutableList<MonthWrapper> {
    val boundedMonthWrappers = mutableListOf<MonthWrapper>()
    forEach {
        val dates = mutableListOf<DateWrapper>()
        it.forEach {
            dates.add(DateWrapper(it, isCollapsed = isCollapse))
        }
        val month = findTheMonth(dates)
        boundedMonthWrappers.add(MonthWrapper(month, dates))
    }
    return boundedMonthWrappers
}

private fun findTheMonth(dates: MutableList<DateWrapper>): MonthYear {
    val mp = mutableMapOf<Month, Pair<Int, Int>>()
    dates.forEach {
        for (prop in DateWrapper::class.memberProperties) {
            if (prop.name == "date") {
                if (mp.containsKey((prop.get(it) as LocalDate).month)) {
                    mp[(prop.get(it) as LocalDate).month] =
                        Pair(
                            (mp[(prop.get(it) as LocalDate).month]!!.first ?: 0) + 1,
                            (prop.get(it) as LocalDate).year
                        )
                } else {
                    mp[(prop.get(it) as LocalDate).month] =
                        Pair(1, (prop.get(it) as LocalDate).year)
                }
            }
        }
    }
    var month = Month.APRIL
    var year = 2021
    val result = mp.toList().sortedByDescending { (_, value) -> value.first }.toMap()
    for (m in result) {
        month = m.key
        year = m.value.second
        break
    }
    return MonthYear(month, year)
}

fun MutableList<MonthWrapper>.changeConfig(
    isCollapse: Boolean,
    item: List<DateWrapper>
): MutableList<MonthWrapper> {
    this.filter { it.dates == item }.firstOrNull().also {
        it?.let {
            val newDateWrapper: MutableList<DateWrapper> = mutableListOf()
            it.dates.forEach {
                newDateWrapper.add(DateWrapper(it.date, isCollapsed = isCollapse))
            }
            this.mapIndexed { index, list ->
                if (list.dates == item) {
                    this[index].dates = newDateWrapper
                }
            }
        } ?: return this
    }
    return this
}

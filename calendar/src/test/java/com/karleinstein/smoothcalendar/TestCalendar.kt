package com.karleinstein.smoothcalendar

import junit.framework.Assert
import org.junit.Test
import org.threeten.bp.LocalDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TestCalendar {

    @Test
    fun convertDate_toCalendar() {
        val test = listOf(
            "1-May-2022",
            "2-May-2022",
            "3-May-2022",
            "4-May-2022",
            "12-April-2022",
            "13-April-2022",
            "14-April-2022",
            "15-April-2022",
            "22-May-2022"
        )
        val timeLong = mutableListOf<Long>()
        test.forEach {
            val f = SimpleDateFormat("dd-MMM-yyyy")
            try {
                val d: Date = f.parse(it)
                val milliseconds: Long = d.time
                timeLong.add(milliseconds)
                println(milliseconds)
            } catch (e: ParseException) {
                e.printStackTrace()
                Assert.fail()
            }
        }
        val res = convertLongToDateWrapper(timeLong)
    }

    private fun convertLongToDateWrapper(input: MutableList<Long>): MutableList<DateWrapper> {
        return mutableListOf<DateWrapper>().apply {
            input.forEach {
                val cal = Calendar.getInstance()
                cal.timeInMillis = it
                add(
                    DateWrapper(
                        LocalDate.of(
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                    )
                )
            }
        }
    }
}

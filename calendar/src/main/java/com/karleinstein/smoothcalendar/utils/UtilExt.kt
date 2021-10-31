package com.karleinstein.smoothcalendar.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.karleinstein.smoothcalendar.SmoothCalendar
import org.threeten.bp.LocalDate

fun SnapHelper.getSnapPosition(smoothCalendar: SmoothCalendar): Int{

    val layoutManager = smoothCalendar.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun LocalDate.isToday(): Boolean {
    val today = LocalDate.now()
    if (this == today) {
        return true
    }
    return false
}
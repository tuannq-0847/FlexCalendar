package com.karleinstein.smoothcalendar

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class SmoothCalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
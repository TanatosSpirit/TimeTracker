// presentation/common/utils/DateUtils.kt
package com.example.timetracker.presentation.common.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDisplayDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun formatDayString(date: Date): String {
        return dayFormat.format(date)
    }

    fun isToday(date: Date): Boolean {
        return formatDayString(date) == formatDayString(Date())
    }

    fun addDays(date: Date, days: Int): Date {
        return Date(date.time + days * 24 * 60 * 60 * 1000L)
    }
}

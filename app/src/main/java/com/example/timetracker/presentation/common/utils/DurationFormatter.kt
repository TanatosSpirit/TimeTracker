// presentation/common/utils/DurationFormatter.kt
package com.example.timetracker.presentation.common.utils

object DurationFormatter {

    fun format(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return when {
            hours > 0 -> String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60)
            minutes > 0 -> String.format("%dm %ds", minutes, seconds % 60)
            else -> String.format("%ds", seconds)
        }
    }

    fun formatTime(timestamp: Long): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return String.format("%02d:%02d",
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE)
        )
    }
}

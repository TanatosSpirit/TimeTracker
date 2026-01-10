// domain/model/TimeEntry.kt
package com.example.timetracker.domain.model

/**
 * Domain модель записи времени.
 * Не зависит от Room, SQLite или других деталей реализации.
 */
data class TimeEntry(
    val id: Long,
    val activityId: Long,
    val startTime: Long,
    val endTime: Long?,
    val date: String
) {
    /**
     * Проверяет, активна ли запись (не завершена)
     */
    val isActive: Boolean
        get() = endTime == null

    /**
     * Вычисляет длительность записи
     * @param currentTime текущее время для активных записей
     */
    fun duration(currentTime: Long = System.currentTimeMillis()): Long {
        val end = endTime ?: currentTime
        return end - startTime
    }
}
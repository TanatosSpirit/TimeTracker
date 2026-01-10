// domain/model/HourSegment.kt
package com.example.timetracker.domain.model

/**
 * Сегмент записи времени в пределах одного часа.
 * Используется для отображения активностей на таймлайне.
 */
data class HourSegment(
    val entry: TimeEntry,      // Domain модель вместо Entity
    val hour: Int,             // Час дня (0-23)
    val startMinute: Int,      // Минута начала в пределах часа (0-59)
    val endMinute: Int         // Минута окончания в пределах часа (1-60)
)
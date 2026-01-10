// domain/usecase/entry/SplitEntryToSegmentsUseCase.kt
package com.example.timetracker.domain.usecase.entry

import com.example.timetracker.domain.model.HourSegment
import com.example.timetracker.domain.model.TimeEntry
import java.util.*
import javax.inject.Inject

/**
 * Use Case для разбиения записи времени на часовые сегменты.
 * Это чистая бизнес-логика, не зависящая от Android Framework.
 */
class SplitEntryToSegmentsUseCase @Inject constructor() {

    /**
     * Разбивает запись на сегменты по часам
     *
     * @param entry запись времени
     * @param selectedDate выбранная дата для отображения
     * @param currentTime текущее время (для активных записей)
     * @return список сегментов по часам
     */
    operator fun invoke(
        entry: TimeEntry,
        selectedDate: Date,
        currentTime: Long = System.currentTimeMillis()
    ): List<HourSegment> {
        val endTime = entry.endTime ?: currentTime
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val result = mutableListOf<HourSegment>()

        for (hour in 0..23) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val hourStart = calendar.timeInMillis
            val hourEnd = hourStart + 60 * 60 * 1000

            // Проверяем, пересекается ли запись с этим часом
            if (entry.startTime < hourEnd && endTime > hourStart) {
                val segmentStart = maxOf(entry.startTime, hourStart)
                val segmentEnd = minOf(endTime, hourEnd)

                val startMinute = ((segmentStart - hourStart) / 60000).toInt()
                val endMinute = ((segmentEnd - hourStart) / 60000)
                    .toInt()
                    .coerceAtLeast(startMinute + 1)

                result += HourSegment(
                    entry = entry,
                    hour = hour,
                    startMinute = startMinute,
                    endMinute = endMinute
                )
            }
        }

        return result
    }
}
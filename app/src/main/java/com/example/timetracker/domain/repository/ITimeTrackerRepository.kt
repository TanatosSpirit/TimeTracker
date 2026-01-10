// domain/repository/ITimeTrackerRepository.kt
package com.example.timetracker.domain.repository

import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.model.TimeEntry
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория в domain слое.
 * Определяет контракт для работы с данными, используя domain модели.
 * Реализация находится в data слое.
 */
interface ITimeTrackerRepository {

    // ========== Activity ==========

    /**
     * Получить все активности
     */
    fun getAllActivities(): Flow<List<Activity>>

    /**
     * Создать новую активность
     */
    suspend fun createActivity(name: String, color: Long, timestamp: Long): Long

    /**
     * Запустить активность (создать запись времени)
     */
    suspend fun startActivity(activityId: Long)

    // ========== TimeEntry ==========

    /**
     * Получить записи за конкретную дату
     */
    fun getEntriesByDate(date: String): Flow<List<TimeEntry>>

    /**
     * Получить активные (незавершённые) записи
     */
    fun getActiveEntries(): Flow<List<TimeEntry>>

    suspend fun toggleActivity(activityId: Long)
    suspend fun stopActivity(activityId: Long)
}
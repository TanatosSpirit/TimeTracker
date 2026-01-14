// data/repository/TimeTrackerRepositoryImpl.kt
package com.example.timetracker.data.repository

import com.example.timetracker.data.local.dao.ActivityDao
import com.example.timetracker.data.local.dao.TimeEntryDao
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity
import com.example.timetracker.data.mapper.ActivityMapper
import com.example.timetracker.data.mapper.TimeEntryMapper
import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.model.TimeEntry
import com.example.timetracker.domain.repository.ITimeTrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Реализация репозитория.
 * Работает с Entity (data слой) и конвертирует в Domain модели.
 */
class TimeTrackerRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    private val timeEntryDao: TimeEntryDao
) : ITimeTrackerRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ========== Activity ==========

    override fun getAllActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities()
            .map { entities -> ActivityMapper.toDomainList(entities) }
    }

    override suspend fun createActivity(name: String, color: Long, timestamp: Long): Long {
        val activity = ActivityEntity(
            name = name,
            color = color,
            usageCount = 1,
            lastUsedAt = timestamp
        )
        return activityDao.insert(activity)
    }

    override suspend fun startActivity(activityId: Long) {
        val now = System.currentTimeMillis()
        val dateString = dateFormat.format(Date(now))

        val entry = TimeEntryEntity(
            activityId = activityId,
            startTime = now,
            endTime = null,
            date = dateString
        )

        timeEntryDao.insert(entry)
//        activityDao.incrementUsageCount(activityId, now)
        activityDao.getActivityById(activityId)?.let { activity ->
            activityDao.update(
                activity.copy(
                    usageCount = activity.usageCount + 1,
                    lastUsedAt = now
                )
            )
        }
    }

    // ========== TimeEntry ==========

    override fun getEntriesByDate(date: String): Flow<List<TimeEntry>> {
        // Вычисляем dayStartTime здесь
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.time = dateFormat.parse(date) ?: Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayStartTime = calendar.timeInMillis

        return timeEntryDao.getEntriesByDate(date, dayStartTime)
            .map { entities -> TimeEntryMapper.toDomainList(entities) }
    }

    override fun getActiveEntries(): Flow<List<TimeEntry>> {
        return timeEntryDao.getActiveEntries()
            .map { entities -> TimeEntryMapper.toDomainList(entities) }
    }

    override suspend fun toggleActivity(activityId: Long) {
        val activeEntries = timeEntryDao.getActiveEntries().first()
        val existingEntry = activeEntries.find { it.activityId == activityId }

        if (existingEntry != null) {
            stopActivity(activityId)
        } else {
            startActivity(activityId)
        }
    }

    override suspend fun stopActivity(activityId: Long) {
        val activeEntries = timeEntryDao.getActiveEntries().first()
        activeEntries.find { it.activityId == activityId }?.let { entry ->
            timeEntryDao.update(entry.copy(endTime = System.currentTimeMillis()))
        }
    }

    override suspend fun deleteTimeEntry(entryId: Long?) {
        timeEntryDao.delete(entryId)
    }
}
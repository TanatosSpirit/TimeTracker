// data/repository/TimeTrackerRepository.kt
package com.example.timetracker.data.repository

import com.example.timetracker.data.local.dao.ActivityDao
import com.example.timetracker.data.local.dao.TimeEntryDao
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeTrackerRepository @Inject constructor(
    private val activityDao: ActivityDao,
    private val timeEntryDao: TimeEntryDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun getAllActivities(): Flow<List<ActivityEntity>> = activityDao.getAllActivities()
    fun getTopActivities(): Flow<List<ActivityEntity>> = activityDao.getTopActivities()
    fun getActiveEntries(): Flow<List<TimeEntryEntity>> = timeEntryDao.getActiveEntries()
//    fun getEntriesByDate(date: String): Flow<List<TimeEntryEntity>> = timeEntryDao.getEntriesByDate(date)
    fun getEntriesByDate(date: String): Flow<List<TimeEntryEntity>> {
        // Вычисляем timestamp начала дня для проверки пересечений
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.time = dateFormat.parse(date) ?: Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayStartTime = calendar.timeInMillis

        return timeEntryDao.getEntriesByDate(date, dayStartTime)
    }
    
//    suspend fun createActivity(name: String, color: Long): Long {
//        return activityDao.insert(
//            ActivityEntity(name = name, color = color)
//        )
//    }
    suspend fun createActivity(name: String, color: Long, lastUsedAt: Long = System.currentTimeMillis()): Long {
        return activityDao.insert(
            ActivityEntity(
                name = name,
                color = color,
                usageCount = 1,
                lastUsedAt = lastUsedAt
            )
        )
    }
    
    suspend fun startActivity(activityId: Long) {
        val now = System.currentTimeMillis()
        val date = dateFormat.format(Date(now))
        
        // Update activity stats
        activityDao.getActivityById(activityId)?.let { activity ->
            activityDao.update(
                activity.copy(
                    usageCount = activity.usageCount + 1,
                    lastUsedAt = now
                )
            )
        }
        
        // Create new entry
        timeEntryDao.insert(
            TimeEntryEntity(
                activityId = activityId,
                startTime = now,
                endTime = null,
                date = date
            )
        )
    }
    
    suspend fun stopActivity(activityId: Long) {
        val activeEntries = timeEntryDao.getActiveEntries().first()
        activeEntries.find { it.activityId == activityId }?.let { entry ->
            timeEntryDao.update(entry.copy(endTime = System.currentTimeMillis()))
        }
    }
    
    suspend fun toggleActivity(activityId: Long) {
        val activeEntries = timeEntryDao.getActiveEntries().first()
        val existingEntry = activeEntries.find { it.activityId == activityId }
        
        if (existingEntry != null) {
            stopActivity(activityId)
        } else {
            startActivity(activityId)
        }
    }
    
    suspend fun isActivityRunning(activityId: Long): Boolean {
        val activeEntries = timeEntryDao.getActiveEntries().first()
        return activeEntries.any { it.activityId == activityId }
    }
}

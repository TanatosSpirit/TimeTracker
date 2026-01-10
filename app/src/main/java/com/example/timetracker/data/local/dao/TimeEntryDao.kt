// data/local/dao/TimeEntryDao.kt
package com.example.timetracker.data.local.dao

import androidx.room.*
import com.example.timetracker.data.local.entities.TimeEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEntryDao {
    @Query("""
        SELECT * FROM time_entries 
        WHERE date = :date 
           OR (date < :date AND endTime IS NULL)
           OR (date < :date AND endTime > :dayStartTime)
        ORDER BY startTime
    """)
    fun getEntriesByDate(date: String, dayStartTime: Long): Flow<List<TimeEntryEntity>>
    
    @Query("SELECT * FROM time_entries WHERE endTime IS NULL")
    fun getActiveEntries(): Flow<List<TimeEntryEntity>>
    
    @Insert
    suspend fun insert(entry: TimeEntryEntity): Long
    
    @Update
    suspend fun update(entry: TimeEntryEntity)
    
    @Delete
    suspend fun delete(entry: TimeEntryEntity)
}

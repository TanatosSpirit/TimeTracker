// data/local/dao/ActivityDao.kt
package com.example.timetracker.data.local.dao

import androidx.room.*
import com.example.timetracker.data.local.entities.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY usageCount DESC, lastUsedAt DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY name ASC")
    fun getAllActivitiesByName(): Flow<List<ActivityEntity>>
    
    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: Long): ActivityEntity?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activity: ActivityEntity): Long
    
    @Update
    suspend fun update(activity: ActivityEntity)
}
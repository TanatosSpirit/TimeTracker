// data/local/entities/TimeEntryEntity.kt
package com.example.timetracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_entries")
data class TimeEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: Long,
    val startTime: Long, // Timestamp in millis
    val endTime: Long?, // null if still running
    val date: String // "yyyy-MM-dd" for grouping
)


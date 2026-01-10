// data/local/entities/ActivityEntity.kt
package com.example.timetracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: Long, // Color as Long (ARGB)
    val usageCount: Int = 0,
    val lastUsedAt: Long = 0 // Timestamp in millis
)


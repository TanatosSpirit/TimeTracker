// data/local/entities/ActivityEntity.kt
package com.example.timetracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "activities",
    foreignKeys = [
        ForeignKey(
            entity = SphereEntity::class,
            parentColumns = ["id"],
            childColumns = ["sphereId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["sphereId"])]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sphereId: Long,
    val usageCount: Int = 0,
    val lastUsedAt: Long = 0 // Timestamp in millis
)


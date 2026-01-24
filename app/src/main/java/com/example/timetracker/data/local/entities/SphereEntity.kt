// data/local/entities/SphereEntity.kt
package com.example.timetracker.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spheres",
    indices = [
        Index(value = ["color", "sphere"], unique = true)
    ]
)
data class SphereEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val color: Long, // ARGB
    val sphere: String? = null,
)
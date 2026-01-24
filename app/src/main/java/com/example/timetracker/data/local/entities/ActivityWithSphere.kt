// data/local/entities/ActivityWithColor.kt
package com.example.timetracker.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation


data class ActivityWithColor(
    @Embedded val activity: ActivityEntity,
    @Relation(
        parentColumn = "sphereId",
        entityColumn = "id"
    )
    val sphere: SphereEntity
)


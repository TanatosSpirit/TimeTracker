package com.example.timetracker.domain.model

import com.example.timetracker.data.local.entities.TimeEntryEntity

data class HourSegment(
    val entry: TimeEntryEntity,
    val hour: Int,          // 0..23
    val startMinute: Int,   // 0..59
    val endMinute: Int      // 1..60
)

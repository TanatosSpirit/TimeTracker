// data/mapper/TimeEntryMapper.kt
package com.example.timetracker.data.mapper

import com.example.timetracker.data.local.entities.TimeEntryEntity
import com.example.timetracker.domain.model.TimeEntry

/**
 * Маппер для конвертации между Entity (data) и Domain моделями TimeEntry
 */
object TimeEntryMapper {

    /**
     * Entity -> Domain
     */
    fun toDomain(entity: TimeEntryEntity): TimeEntry {
        return TimeEntry(
            id = entity.id,
            activityId = entity.activityId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            date = entity.date
        )
    }

    /**
     * Domain -> Entity
     */
    fun toEntity(domain: TimeEntry): TimeEntryEntity {
        return TimeEntryEntity(
            id = domain.id,
            activityId = domain.activityId,
            startTime = domain.startTime,
            endTime = domain.endTime,
            date = domain.date
        )
    }

    /**
     * List<Entity> -> List<Domain>
     */
    fun toDomainList(entities: List<TimeEntryEntity>): List<TimeEntry> {
        return entities.map { toDomain(it) }
    }
}
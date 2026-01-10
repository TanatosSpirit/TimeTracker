// data/mapper/ActivityMapper.kt
package com.example.timetracker.data.mapper

import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.domain.model.Activity

/**
 * Маппер для конвертации между Entity (data) и Domain моделями Activity
 */
object ActivityMapper {

    /**
     * Entity -> Domain
     */
    fun toDomain(entity: ActivityEntity): Activity {
        return Activity(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            usageCount = entity.usageCount,
            lastUsedAt = if (entity.lastUsedAt == 0L) null else entity.lastUsedAt
        )
    }

    /**
     * Domain -> Entity
     */
    fun toEntity(domain: Activity): ActivityEntity {
        return ActivityEntity(
            id = domain.id,
            name = domain.name,
            color = domain.color,
            usageCount = domain.usageCount,
            lastUsedAt = domain.lastUsedAt ?: 0L
        )
    }

    /**
     * List<Entity> -> List<Domain>
     */
    fun toDomainList(entities: List<ActivityEntity>): List<Activity> {
        return entities.map { toDomain(it) }
    }
}
package com.example.timetracker.domain.usecase.activity

import com.example.timetracker.domain.repository.ITimeTrackerRepository
import javax.inject.Inject

/**
 * Use Case обновления названия активности
 */
class UpdateActivityName @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    suspend operator fun invoke(activityId: Long, name: String) {
        repository.updateActivityName(activityId, name)
    }
}
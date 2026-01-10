// domain/usecase/activity/ToggleActivityUseCase.kt
package com.example.timetracker.domain.usecase.activity

import com.example.timetracker.domain.repository.ITimeTrackerRepository
import javax.inject.Inject

class ToggleActivityUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    suspend operator fun invoke(activityId: Long) {
        repository.toggleActivity(activityId)
    }
}
// domain/usecase/activity/GetActivitiesUseCase.kt
package com.example.timetracker.domain.usecase.activity

import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.repository.ITimeTrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения списка всех активностей
 */
class GetActivitiesUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    operator fun invoke(): Flow<List<Activity>> {
        return repository.getAllActivities()
    }
}
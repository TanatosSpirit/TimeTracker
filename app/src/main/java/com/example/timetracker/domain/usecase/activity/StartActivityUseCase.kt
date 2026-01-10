// domain/usecase/activity/StartActivityUseCase.kt
package com.example.timetracker.domain.usecase.activity

import com.example.timetracker.domain.repository.ITimeTrackerRepository
import javax.inject.Inject

/**
 * Use Case для запуска активности (создание записи времени)
 */
class StartActivityUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    /**
     * Запускает активность
     *
     * @param activityId ID активности для запуска
     */
    suspend operator fun invoke(activityId: Long) {
        repository.startActivity(activityId)
    }
}
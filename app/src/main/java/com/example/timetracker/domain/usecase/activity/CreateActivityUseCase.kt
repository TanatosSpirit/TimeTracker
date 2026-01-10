// domain/usecase/activity/CreateActivityUseCase.kt
package com.example.timetracker.domain.usecase.activity

import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.repository.ITimeTrackerRepository
import javax.inject.Inject
import kotlin.random.Random

/**
 * Use Case для создания новой активности
 */
class CreateActivityUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    /**
     * Создаёт новую активность со случайным цветом
     *
     * @param name название активности
     * @param timestamp время создания
     * @return ID созданной активности
     */
    suspend operator fun invoke(name: String, timestamp: Long): Long {
        val color = generateRandomColor()
        return repository.createActivity(
            name = name,
            color = color,
            timestamp = timestamp
        )
    }

    private fun generateRandomColor(): Long {
        return Activity.COLOR_PALETTE[Random.nextInt(Activity.COLOR_PALETTE.size)]
    }
}
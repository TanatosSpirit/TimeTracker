// domain/usecase/activity/GetActiveEntriesUseCase.kt
package com.example.timetracker.domain.usecase.activity

import com.example.timetracker.domain.model.TimeEntry
import com.example.timetracker.domain.repository.ITimeTrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения активных (незавершённых) записей времени
 */
class GetActiveEntriesUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    operator fun invoke(): Flow<List<TimeEntry>> {
        return repository.getActiveEntries()
    }
}
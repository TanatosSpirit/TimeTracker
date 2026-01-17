package com.example.timetracker.domain.usecase.entry

import com.example.timetracker.domain.repository.ITimeTrackerRepository
import javax.inject.Inject

/**
 * Use Case обновления времени активности в базе
 */
class UpdateEntryTimesUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    suspend operator fun invoke(entryId: Long, startTime: Long, endTime: Long?) {
        repository.updateEntryTimes(entryId, startTime, endTime)
    }
}
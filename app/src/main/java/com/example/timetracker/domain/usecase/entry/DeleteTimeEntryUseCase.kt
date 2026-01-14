package com.example.timetracker.domain.usecase.entry

import com.example.timetracker.domain.repository.ITimeTrackerRepository
import javax.inject.Inject

/**
 * Use Case для удаления записи о времени
 */
class DeleteTimeEntryUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    suspend operator fun invoke(entryId: Long?) {
        repository.deleteTimeEntry(entryId)
    }
}
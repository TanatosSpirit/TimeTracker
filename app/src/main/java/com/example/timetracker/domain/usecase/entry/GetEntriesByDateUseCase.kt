// domain/usecase/entry/GetEntriesByDateUseCase.kt
package com.example.timetracker.domain.usecase.entry

import com.example.timetracker.domain.model.TimeEntry
import com.example.timetracker.domain.repository.ITimeTrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения записей времени за конкретную дату
 */
class GetEntriesByDateUseCase @Inject constructor(
    private val repository: ITimeTrackerRepository
) {
    /**
     * Получает записи за указанную дату
     *
     * @param dateString дата в формате yyyy-MM-dd
     * @return Flow со списком записей
     */
    operator fun invoke(dateString: String): Flow<List<TimeEntry>> {
        return repository.getEntriesByDate(dateString)
    }
}
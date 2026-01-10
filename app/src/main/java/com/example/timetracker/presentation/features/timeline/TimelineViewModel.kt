// presentation/features/timeline/TimelineViewModel.kt
package com.example.timetracker.presentation.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.domain.model.HourSegment
import com.example.timetracker.domain.usecase.activity.*
import com.example.timetracker.domain.usecase.entry.GetEntriesByDateUseCase
import com.example.timetracker.domain.usecase.entry.SplitEntryToSegmentsUseCase
import com.example.timetracker.presentation.common.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getActivitiesUseCase: GetActivitiesUseCase,
    private val getActiveEntriesUseCase: GetActiveEntriesUseCase,
    private val getEntriesByDateUseCase: GetEntriesByDateUseCase,
    private val createActivityUseCase: CreateActivityUseCase,
    private val startActivityUseCase: StartActivityUseCase,
    private val splitEntryToSegmentsUseCase: SplitEntryToSegmentsUseCase
) : ViewModel() {

    // ========== State ==========

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate = _selectedDate.asStateFlow()

    private val _scrollToCurrentTime = MutableStateFlow<Long?>(null)
    val scrollToCurrentTime: StateFlow<Long?> = _scrollToCurrentTime.asStateFlow()

    // ========== Data Streams ==========

    val currentDateString = selectedDate.map { date ->
        DateUtils.formatDayString(date)
    }.stateIn(viewModelScope, SharingStarted.Lazily, DateUtils.formatDayString(Date()))

    val timeEntries = currentDateString.flatMapLatest { dateStr ->
        getEntriesByDateUseCase(dateStr)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val hourSegments: StateFlow<List<HourSegment>> =
        combine(timeEntries, selectedDate) { entries, date ->
            val now = System.currentTimeMillis()
            entries.flatMap { entry ->
                splitEntryToSegmentsUseCase(entry, date, now)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activities = getActivitiesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeEntries = getActiveEntriesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ========== Initialization ==========

    init {
        _scrollToCurrentTime.value = System.currentTimeMillis()
    }

    // ========== Actions ==========

    /**
     * Создать и запустить новую активность
     */
    fun createAndStartActivity(name: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val activityId = createActivityUseCase(name, now)
            startActivityUseCase(activityId)
        }
    }

    /**
     * Перейти к предыдущему дню
     */
    fun previousDay() {
        _selectedDate.value = DateUtils.addDays(_selectedDate.value, -1)
    }

    /**
     * Перейти к следующему дню
     */
    fun nextDay() {
        _selectedDate.value = DateUtils.addDays(_selectedDate.value, 1)
    }

    /**
     * Перейти к сегодняшнему дню и проскроллить к текущему времени
     */
    fun goToToday() {
        _selectedDate.value = Date()
        _scrollToCurrentTime.value = System.currentTimeMillis()
    }

    /**
     * Сбросить состояние прокрутки после завершения
     */
    fun onScrollCompleted() {
        _scrollToCurrentTime.value = null
    }
}
// presentation/MainViewModel.kt
package com.example.timetracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.local.entities.TimeEntryEntity
import com.example.timetracker.data.repository.TimeTrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.timetracker.domain.model.HourSegment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TimeTrackerRepository
) : ViewModel() {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate = _selectedDate.asStateFlow()

    private val _scrollToCurrentTime = MutableStateFlow<Long?>(null)
    val scrollToCurrentTime: StateFlow<Long?> = _scrollToCurrentTime.asStateFlow()
    
    val currentDateString = selectedDate.map { date ->
        dateFormat.format(date)
    }.stateIn(viewModelScope, SharingStarted.Lazily, dateFormat.format(Date()))
    
    val timeEntries = currentDateString.flatMapLatest { dateStr ->
        repository.getEntriesByDate(dateStr)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val hourSegments: StateFlow<List<HourSegment>> =
        combine(timeEntries, selectedDate) { entries, date ->

            val now = System.currentTimeMillis()
            entries.flatMap { entry ->
                splitEntryToHourSegments(entry, date, now)
            }

        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val activities = repository.getAllActivities()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val activeEntries = repository.getActiveEntries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        _scrollToCurrentTime.value = System.currentTimeMillis()
    }
    fun createAndStartActivity(name: String) {
        viewModelScope.launch {
            val color = generateRandomColor()
            val now = System.currentTimeMillis()

            // Создаем активность сразу с usageCount=1 и lastUsedAt
            val activityId = repository.createActivity(name, color, now)
            repository.startActivity(activityId)
        }
    }
    
    fun previousDay() {
        _selectedDate.value = Date(_selectedDate.value.time - 24 * 60 * 60 * 1000)
    }
    
    fun nextDay() {
        _selectedDate.value = Date(_selectedDate.value.time + 24 * 60 * 60 * 1000)
    }
    
    fun goToToday() {
        _selectedDate.value = Date()
        _scrollToCurrentTime.value = System.currentTimeMillis()
    }

    fun onScrollCompleted() {
        _scrollToCurrentTime.value = null
    }

    private fun generateRandomColor(): Long {
        val colors = listOf(
            0xFFEF5350, 0xFFEC407A, 0xFFAB47BC, 0xFF7E57C2,
            0xFF5C6BC0, 0xFF42A5F5, 0xFF29B6F6, 0xFF26C6DA,
            0xFF26A69A, 0xFF66BB6A, 0xFF9CCC65, 0xFFD4E157,
            0xFFFFEE58, 0xFFFFCA28, 0xFFFF7043, 0xFF8D6E63
        )
        return colors[Random.nextInt(colors.size)]
    }
    private fun splitEntryToHourSegments(
        entry: TimeEntryEntity,
        selectedDate: Date,
        now: Long
    ): List<HourSegment> {

        val endTime = entry.endTime ?: now
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val result = mutableListOf<HourSegment>()

        for (hour in 0..23) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val hourStart = calendar.timeInMillis
            val hourEnd = hourStart + 60 * 60 * 1000

            if (entry.startTime < hourEnd && endTime > hourStart) {
                val segmentStart = maxOf(entry.startTime, hourStart)
                val segmentEnd = minOf(endTime, hourEnd)

                val startMinute = ((segmentStart - hourStart) / 60000).toInt()
                val endMinute = ((segmentEnd - hourStart) / 60000)
                    .toInt()
                    .coerceAtLeast(startMinute + 1)

                result += HourSegment(
                    entry = entry,
                    hour = hour,
                    startMinute = startMinute,
                    endMinute = endMinute
                )
            }
        }

        return result
    }

}
// presentation/features/timeline/components/DayTimeline.kt
package com.example.timetracker.presentation.features.timeline.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity
import com.example.timetracker.domain.model.HourSegment
import java.util.*

@Composable
fun DayTimeline(
    hourSegments: List<HourSegment>,
    activities: List<ActivityEntity>,
    activeEntries: List<TimeEntryEntity>,
    selectedDate: Date,
    scrollToTime: Long?,
    onScrollCompleted: () -> Unit,
    onEntryClick: (TimeEntryEntity, ActivityEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val activitiesMap = activities.associateBy { it.id }
    val listState = rememberLazyListState()

    // Автопрокрутка к текущему времени
    LaunchedEffect(scrollToTime) {
        scrollToTime?.let { timestamp ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            listState.animateScrollToItem((hour - 2).coerceIn(0, 23))
            onScrollCompleted()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState
    ) {
        items(24) { hour ->
            HourRow(
                hour = hour,
                segments = hourSegments.filter { it.hour == hour },
                activitiesMap = activitiesMap,
                activeEntries = activeEntries,
                onEntryClick = onEntryClick
            )
        }
    }
}

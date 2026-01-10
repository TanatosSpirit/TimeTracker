// presentation/features/timeline/components/HourRow.kt
package com.example.timetracker.presentation.features.timeline.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.model.TimeEntry
import com.example.timetracker.domain.model.HourSegment

@Composable
fun HourRow(
    hour: Int,
    segments: List<HourSegment>,
    activitiesMap: Map<Long, Activity>,
    activeEntries: List<TimeEntry>,
    onEntryClick: (TimeEntry, Activity) -> Unit,
    modifier: Modifier = Modifier,
    hourHeightDp: androidx.compose.ui.unit.Dp = 160.dp
) {
    // Группируем по entry — каждая активность своя колонка
    val groupedByEntry = segments.groupBy { it.entry.id }
    val columns = groupedByEntry.values.toList()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeightDp)
                .padding(horizontal = 8.dp)
        ) {
            // Метка времени
            Text(
                text = String.format("%02d:00", hour),
                modifier = Modifier.width(60.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )

            // Колонки с активностями
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                columns.forEach { entrySegments ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        entrySegments.forEach { segment ->
                            val entry = segment.entry
                            val activity = activitiesMap[entry.activityId] ?: return@forEach

                            val startFraction = segment.startMinute / 60f
                            val lengthFraction = (segment.endMinute - segment.startMinute) / 60f

                            TimeEntrySegment(
                                activity = activity,
                                entry = entry,
                                isActive = activeEntries.any { it.id == entry.id },
                                startFraction = startFraction,
                                lengthFraction = lengthFraction,
                                hourHeightDp = hourHeightDp,
                                onClick = { onEntryClick(entry, activity) }
                            )
                        }
                    }
                }
            }
        }

        Divider(thickness = 0.5.dp)
    }
}

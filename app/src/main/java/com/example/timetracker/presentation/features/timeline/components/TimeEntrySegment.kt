// presentation/features/timeline/components/TimeEntrySegment.kt
package com.example.timetracker.presentation.features.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity
import com.example.timetracker.presentation.common.utils.DurationFormatter
import kotlinx.coroutines.delay

@Composable
fun ColumnScope.TimeEntrySegment(
    activity: ActivityEntity,
    entry: TimeEntryEntity,
    isActive: Boolean,
    startFraction: Float,
    lengthFraction: Float,
    hourHeightDp: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Для активных записей обновляем время каждую секунду
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                delay(1000)
                currentTime = System.currentTimeMillis()
            }
        }
    }

    val entryEnd = if (isActive) currentTime else (entry.endTime ?: entry.startTime)
    val totalDuration = entryEnd - entry.startTime
    val durationText = DurationFormatter.format(totalDuration)

    // Используем точную высоту вместо weight() для устранения дискретности
    val segmentHeightDp = hourHeightDp * lengthFraction
    val spacerTopHeightDp = hourHeightDp * startFraction

    // Пустое пространство до начала активности
    if (startFraction > 0) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacerTopHeightDp)
        )
    }

    // Сама активность
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(segmentHeightDp)
            .background(Color(activity.color))
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = "${activity.name} $durationText" + if (isActive) " ⏱" else "",
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 14.sp
        )
    }

    // Пустое пространство после активности
    val remainingFraction = 1f - startFraction - lengthFraction
    if (remainingFraction > 0.001f) {
        val spacerBottomHeightDp = hourHeightDp * remainingFraction
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacerBottomHeightDp)
        )
    }
}

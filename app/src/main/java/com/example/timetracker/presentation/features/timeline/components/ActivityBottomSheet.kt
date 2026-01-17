// presentation/features/timeline/components/ActivityBottomSheet.kt
package com.example.timetracker.presentation.features.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.model.TimeEntry
import com.example.timetracker.presentation.common.utils.DurationFormatter
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityBottomSheet(
    entry: TimeEntry,
    activity: Activity,
    isActive: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onTimeUpdate: (Long, Long?) -> Unit = { _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState()

    // Состояние выбранного времени (инициализируем данными из entry)
    var selectedStartTime by remember { mutableLongStateOf(entry.startTime) }
    var selectedEndTime by remember { mutableStateOf(entry.endTime) }

    // Состояние видимости пикеров
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Для активных записей (таймер)
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                delay(1000)
                currentTime = System.currentTimeMillis()
            }
        }
    }

    val startTimeText = DurationFormatter.formatTime(selectedStartTime)
    val endTimeText = if (isActive) {
        "..."
    } else {
        selectedEndTime?.let { DurationFormatter.formatTime(it) } ?: "..."
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Блок редактирования времени
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Время начала (всегда редактируемое)
                    TextButton(onClick = { showStartTimePicker = true }) {
                        Text(text = startTimeText, fontSize = 16.sp)
                    }

                    Text("-", color = Color.Gray)

                    // Время конца (редактируемое только если не активно)
                    TextButton(
                        onClick = { if (!isActive) showEndTimePicker = true },
                        enabled = !isActive
                    ) {
                        Text(
                            text = endTimeText,
                            fontSize = 16.sp,
                            color = if (isActive) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isActive) {
                Text(
                    text = "Active now ⏱",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка сохранения вынесена так, чтобы работать и для активных, и для завершенных
            val isChanged = if (isActive) {
                selectedStartTime != entry.startTime
            } else {
                selectedStartTime != entry.startTime || selectedEndTime != entry.endTime
            }

            if (isChanged) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Если активно — всегда шлем null в endTime, если завершено — выбранное значение
                        val endTimeToSave = if (isActive) null else selectedEndTime
                        onTimeUpdate(selectedStartTime, endTimeToSave)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить запись",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }

    // Диалоги выбора времени
    if (showStartTimePicker) {
        TimePickerDialogWrapper(
            initialTime = selectedStartTime,
            onTimeSelected = { newTime ->
                selectedStartTime = newTime
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialogWrapper(
            initialTime = selectedEndTime ?: System.currentTimeMillis(),
            onTimeSelected = { newTime ->
                selectedEndTime = newTime
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

// Вспомогательный компонент для TimePicker (Material 3)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogWrapper(
    initialTime: Long,
    onTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = initialTime }
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val newCalendar = Calendar.getInstance().apply {
                    timeInMillis = initialTime
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                }
                onTimeSelected(newCalendar.timeInMillis)
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}
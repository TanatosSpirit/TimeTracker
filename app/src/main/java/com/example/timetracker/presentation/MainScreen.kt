// presentation/MainScreen.kt
package com.example.timetracker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.timetracker.ui.theme.TimeTrackerTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min
import androidx.compose.foundation.lazy.rememberLazyListState
import java.util.Calendar
import com.example.timetracker.domain.model.HourSegment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val hourSegments by viewModel.hourSegments.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val activeEntries by viewModel.activeEntries.collectAsState()
    val scrollToCurrentTime by viewModel.scrollToCurrentTime.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()

    TimeTrackerTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Time Tracker") },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, "Add Activity")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                DateNavigationBar(
                    date = selectedDate,
                    onPreviousDay = { viewModel.previousDay() },
                    onNextDay = { viewModel.nextDay() },
                    onToday = { viewModel.goToToday() }
                )

                DayTimeline(
                    hourSegments = hourSegments,
                    activities = activities,
                    activeEntries = activeEntries,
                    selectedDate = selectedDate,
                    scrollToTime = scrollToCurrentTime,
                    onScrollCompleted = { viewModel.onScrollCompleted() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showAddDialog) {
        AddActivityDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.createAndStartActivity(name)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DateNavigationBar(
    date: Date,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    val isToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) ==
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.Default.KeyboardArrowLeft, "Previous day")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dateFormat.format(date),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            if (!isToday) {
                TextButton(onClick = onToday) {
                    Text("Today")
                }
            }
        }

        IconButton(onClick = onNextDay) {
            Icon(Icons.Default.KeyboardArrowRight, "Next day")
        }
    }
}

@Composable
fun DayTimeline(
    hourSegments: List<HourSegment>,
    activities: List<ActivityEntity>,
    activeEntries: List<TimeEntryEntity>,
    selectedDate: Date,
    modifier: Modifier = Modifier,
    scrollToTime: Long? = null,
    onScrollCompleted: () -> Unit = {}
) {
    val activitiesMap = activities.associateBy { it.id }
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToTime) {
        scrollToTime?.let { timestamp ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            listState.animateScrollToItem((hour - 2).coerceIn(0, 23))
            onScrollCompleted()
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
        items(24) { hour ->
            HourRow(
                hour = hour,
                segments = hourSegments.filter { it.hour == hour },
                activitiesMap = activitiesMap,
                activeEntries = activeEntries,
                selectedDate = selectedDate,
                currentTime = if (isToday(selectedDate)) System.currentTimeMillis() else null
            )
        }
    }
}

private fun isToday(date: Date): Boolean {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(date) == dateFormat.format(Date())
}

private fun calculateOverlapLevels(
    entries: List<TimeEntryEntity>,
    hourStart: Long,
    hourEnd: Long
): List<Pair<TimeEntryEntity, Int>> {
    val entriesInHour = entries.filter { entry ->
        val entryEnd = entry.endTime ?: System.currentTimeMillis()
        entry.startTime < hourEnd && entryEnd > hourStart
    }

    return entriesInHour.map { entry ->
        val entryEnd = entry.endTime ?: System.currentTimeMillis()

        // Считаем, со сколькими записями пересекается эта запись
        val overlappingCount = entriesInHour.count { other ->
            if (other.id == entry.id) return@count false
            val otherEnd = other.endTime ?: System.currentTimeMillis()

            val entryStartInHour = max(entry.startTime, hourStart)
            val entryEndInHour = min(entryEnd, hourEnd)
            val otherStartInHour = max(other.startTime, hourStart)
            val otherEndInHour = min(otherEnd, hourEnd)

            entryStartInHour < otherEndInHour && entryEndInHour > otherStartInHour
        }

        entry to overlappingCount
    }
}

@Composable
fun HourRow(
    hour: Int,
    segments: List<HourSegment>,
    activitiesMap: Map<Long, ActivityEntity>,
    activeEntries: List<TimeEntryEntity>,
    selectedDate: Date,
    currentTime: Long?
) {
    val hourHeightDp = 160.dp

    // Группируем по entry — каждая активность своя колонка
    val groupedByEntry = segments.groupBy { it.entry.id }
    val columns = groupedByEntry.values.toList()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(hourHeightDp)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = String.format("%02d:00", hour),
            modifier = Modifier.width(60.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )

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
                        val lengthFraction =
                            (segment.endMinute - segment.startMinute) / 60f

                        TimeEntrySegment(
                            activity = activity,
                            entry = entry,
                            isActive = activeEntries.any { it.id == entry.id },
                            startFraction = startFraction,
                            lengthFraction = lengthFraction,
                            hourHeightDp = hourHeightDp
                        )
                    }
                }
            }
        }
    }

    Divider(thickness = 0.5.dp)
}

@Composable
fun ColumnScope.TimeEntrySegment(
    activity: ActivityEntity,
    entry: TimeEntryEntity,
    isActive: Boolean,
    startFraction: Float,
    lengthFraction: Float,
    hourHeightDp: Dp = 160.dp  // НОВЫЙ ПАРАМЕТР
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

    // Полная длительность записи для отображения
    val totalDuration = entryEnd - entry.startTime
    val durationText = formatDuration(totalDuration)

    // ИСПРАВЛЕНИЕ: Используем точную высоту вместо weight()
    // Это устраняет проблему дискретности
    val segmentHeightDp = hourHeightDp * lengthFraction
    val spacerTopHeightDp = hourHeightDp * startFraction

    // Пустое пространство до начала активности
    if (startFraction > 0) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacerTopHeightDp)  // ТОЧНАЯ ВЫСОТА вместо weight
        )
    }

    // Сама активность
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(segmentHeightDp)  // ТОЧНАЯ ВЫСОТА вместо weight
            .background(Color(activity.color))
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
                .height(spacerBottomHeightDp)  // ТОЧНАЯ ВЫСОТА вместо weight
        )
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60)
        minutes > 0 -> String.format("%dm %ds", minutes, seconds % 60)
        else -> String.format("%ds", seconds)
    }
}

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Activity") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Activity name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getHourTimestamp(date: Date, hour: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
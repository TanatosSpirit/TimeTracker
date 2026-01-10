// presentation/MainScreen.kt
package com.example.timetracker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.timetracker.ui.theme.TimeTrackerTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.offset
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val timeEntries by viewModel.timeEntries.collectAsState()
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
                    timeEntries = timeEntries,
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

//@Composable
//fun DayTimeline(
//    timeEntries: List<TimeEntryEntity>,
//    activities: List<ActivityEntity>,
//    activeEntries: List<TimeEntryEntity>,
//    selectedDate: Date,
//    modifier: Modifier = Modifier,
//    scrollToTime: Long? = null,
//    onScrollCompleted: () -> Unit = {}
//) {
//    val activitiesMap = activities.associateBy { it.id }
//
//    // Группируем пересекающиеся активности
//    val entryGroups = groupOverlappingEntries(timeEntries)
//
//    val listState = rememberLazyListState()
//    LaunchedEffect(scrollToTime) {
//        scrollToTime?.let { timestamp ->
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = timestamp
//            val hour = calendar.get(Calendar.HOUR_OF_DAY)
//            val minute = calendar.get(Calendar.MINUTE)
//
//            // Вычисляем индекс для центрирования
//            // Пытаемся центрировать, но ограничиваем диапазон [0, 23]
//            val targetIndex = (hour - 2).coerceIn(0, 23)
//
//            listState.animateScrollToItem(targetIndex)
//            onScrollCompleted()
//        }
//    }
//
//    LazyColumn(modifier = modifier.fillMaxSize(),
//        state = listState)
//    {
//        items(24) { hour ->
//            HourRow(
//                hour = hour,
//                entryGroups = entryGroups,
//                activitiesMap = activitiesMap,
//                activeEntries = activeEntries,
//                selectedDate = selectedDate,
//                currentTime = if (isToday(selectedDate)) System.currentTimeMillis() else null
//            )
//        }
//    }
//}

@Composable
fun DayTimeline(
    timeEntries: List<TimeEntryEntity>,
    activities: List<ActivityEntity>,
    activeEntries: List<TimeEntryEntity>,
    selectedDate: Date,
    modifier: Modifier = Modifier,
    scrollToTime: Long? = null,
    onScrollCompleted: () -> Unit = {}
) {
    val activitiesMap = activities.associateBy { it.id }

    // УДАЛИТЬ эту строку:
    // val entryGroups = groupOverlappingEntries(timeEntries)

    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTime) {
        scrollToTime?.let { timestamp ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val targetIndex = (hour - 2).coerceIn(0, 23)

            listState.animateScrollToItem(targetIndex)
            onScrollCompleted()
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
        items(24) { hour ->
            HourRow(
                hour = hour,
                timeEntries = timeEntries,  // ИЗМЕНИТЬ: передаем timeEntries вместо entryGroups
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

// Группируем записи, которые пересекаются по времени
//private fun groupOverlappingEntries(entries: List<TimeEntryEntity>): List<List<TimeEntryEntity>> {
//    if (entries.isEmpty()) return emptyList()
//
//    val sorted = entries.sortedBy { it.startTime }
//    val groups = mutableListOf<MutableList<TimeEntryEntity>>()
//
//    sorted.forEach { entry ->
//        val entryEnd = entry.endTime ?: System.currentTimeMillis()
//
//        // Ищем группу, с которой пересекается эта запись
//        val overlappingGroup = groups.find { group ->
//            group.any { existing ->
//                val existingEnd = existing.endTime ?: System.currentTimeMillis()
//                // Проверяем пересечение
//                entry.startTime < existingEnd && entryEnd > existing.startTime
//            }
//        }
//
//        if (overlappingGroup != null) {
//            overlappingGroup.add(entry)
//        } else {
//            groups.add(mutableListOf(entry))
//        }
//    }
//
//    return groups
//}

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


//@Composable
//fun HourRow(
//    hour: Int,
//    entryGroups: List<List<TimeEntryEntity>>,
//    activitiesMap: Map<Long, ActivityEntity>,
//    activeEntries: List<TimeEntryEntity>,
//    selectedDate: Date,
//    currentTime: Long? = null
//) {
//    val hourStart = getHourTimestamp(selectedDate, hour)
//    val hourEnd = getHourTimestamp(selectedDate, hour + 1)
//    val hourDuration = hourEnd - hourStart
//
//    val currentTimePosition = currentTime?.let {
//        if (it in hourStart until hourEnd) {
//            (it - hourStart).toFloat() / hourDuration
//        } else null
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(160.dp)  // УВЕЛИЧИЛИ В 2 РАЗА для лучшей детализации
//            .padding(horizontal = 8.dp)
//    ) {
//        // Hour label
//        Text(
//            text = String.format("%02d:00", hour),
//            modifier = Modifier
//                .width(60.dp)
//                .padding(top = 4.dp),
//            fontSize = 12.sp,
//            color = Color.Gray
//        )
//
//        Box(  // ИЗМЕНИТЬ с Row на Box
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight()
//                .background(MaterialTheme.colorScheme.surface)
//        ) {
//            // Timeline area - используем Row для горизонтального размещения групп
//            Row(
//                modifier = Modifier
//                    .fillMaxHeight()
//            ) {
//                entryGroups.forEach { group ->
//                    // Проверяем, пересекается ли хоть одна запись из группы с этим часом
//                    val entriesInThisHour = group.filter { entry ->
//                        val entryEnd = entry.endTime ?: System.currentTimeMillis()
//                        entry.startTime < hourEnd && entryEnd > hourStart
//                    }
//
//                    if (entriesInThisHour.isNotEmpty()) {
//                        // Каждая группа занимает равную долю ширины
//                        Column(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxHeight()
//                        ) {
//                            entriesInThisHour.forEach { entry ->
//                                val activity = activitiesMap[entry.activityId]
//                                if (activity != null) {
//                                    val entryEnd = entry.endTime ?: System.currentTimeMillis()
//                                    val isActive = activeEntries.any { it.id == entry.id }
//
//                                    // Вычисляем видимую часть в пределах текущего часа
//                                    val segmentStart = max(entry.startTime, hourStart)
//                                    val segmentEnd = min(entryEnd, hourEnd)
//
//                                    // Позиция начала сегмента относительно начала часа (0.0 - 1.0)
//                                    val startFraction =
//                                        (segmentStart - hourStart).toFloat() / hourDuration
//
//                                    // Длина сегмента относительно часа (0.0 - 1.0)
//                                    val lengthFraction =
//                                        (segmentEnd - segmentStart).toFloat() / hourDuration
//
//                                    TimeEntrySegment(
//                                        activity = activity,
//                                        entry = entry,
//                                        isActive = isActive,
//                                        startFraction = startFraction,
//                                        lengthFraction = lengthFraction,
//                                        hourHeightDp = 160.dp  // Передаем высоту для расчетов
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            // ДОБАВИТЬ красную линию текущего времени
//            currentTimePosition?.let { position ->
//                Canvas(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(2.dp)
//                        .offset(y = (160.dp * position))
//                ) {
//                    drawLine(
//                        color = Color.Red,
//                        start = Offset(0f, 0f),
//                        end = Offset(size.width, 0f),
//                        strokeWidth = 4f,
//                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//                    )
//                }
//            }
//        }
//    }
//
//    Divider(color = Color.LightGray, thickness = 0.5.dp)
//}

@Composable
fun HourRow(
    hour: Int,
    timeEntries: List<TimeEntryEntity>,
    activitiesMap: Map<Long, ActivityEntity>,
    activeEntries: List<TimeEntryEntity>,
    selectedDate: Date,
    currentTime: Long? = null
) {
    val hourStart = getHourTimestamp(selectedDate, hour)
    val hourEnd = getHourTimestamp(selectedDate, hour + 1)
    val hourDuration = hourEnd - hourStart

    val entryLevels = calculateOverlapLevels(timeEntries, hourStart, hourEnd)
    val maxOverlaps = entryLevels.maxOfOrNull { it.second } ?: 0
    val columns = maxOverlaps + 1 // Количество столбцов = максимум пересечений + 1

    val currentTimePosition = currentTime?.let {
        if (it in hourStart until hourEnd) {
            (it - hourStart).toFloat() / hourDuration
        } else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = String.format("%02d:00", hour),
            modifier = Modifier
                .width(60.dp)
                .padding(top = 4.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )

//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight()
//                .background(MaterialTheme.colorScheme.surface)
//        ) {
//            // Для каждой записи определяем её колонку
//            entryLevels.forEach { (entry, _) ->
//                val activity = activitiesMap[entry.activityId] ?: return@forEach
//                val entryEnd = entry.endTime ?: System.currentTimeMillis()
//                val isActive = activeEntries.any { it.id == entry.id }
//
//                // Находим свободную колонку для этой записи
//                val columnIndex = findFreeColumn(entry, entryLevels, hourStart, hourEnd)
//
//                val segmentStart = max(entry.startTime, hourStart)
//                val segmentEnd = min(entryEnd, hourEnd)
//
//                val startFraction = (segmentStart - hourStart).toFloat() / hourDuration
//                val lengthFraction = (segmentEnd - segmentStart).toFloat() / hourDuration
//
//                // Вычисляем ширину и offset для колонки
//                val columnWidth = 1f / columns
//                val columnOffset = columnIndex * columnWidth
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(columnWidth)
//                        .fillMaxHeight()
//                        .offset(x = (columnOffset * 100).dp) // Примерное смещение
//                ) {
//                    Column(modifier = Modifier.fillMaxSize()) {
//                        TimeEntrySegment(
//                            activity = activity,
//                            entry = entry,
//                            isActive = isActive,
//                            startFraction = startFraction,
//                            lengthFraction = lengthFraction,
//                            hourHeightDp = 160.dp
//                        )
//                    }
//                }
//            }
//
//            currentTimePosition?.let { position ->
//                Canvas(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(2.dp)
//                        .offset(y = (160.dp * position))
//                ) {
//                    drawLine(
//                        color = Color.Red,
//                        start = Offset(0f, 0f),
//                        end = Offset(size.width, 0f),
//                        strokeWidth = 4f,
//                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//                    )
//                }
//            }
//        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Если нет записей - пустой Box
            if (entryLevels.isEmpty()) {
                // Пусто
            } else {
                // Используем Row для горизонтального размещения колонок
                Row(modifier = Modifier.fillMaxSize()) {
                    // Создаем columns количество колонок
                    repeat(columns) { columnIndex ->
                        // Находим все записи для этой колонки
                        val entriesInColumn = entryLevels.filter { (entry, _) ->
                            findFreeColumn(entry, entryLevels, hourStart, hourEnd) == columnIndex
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)  // Равномерное распределение
                                .fillMaxHeight()
                        ) {
                            entriesInColumn.forEach { (entry, _) ->
                                val activity = activitiesMap[entry.activityId] ?: return@forEach
                                val entryEnd = entry.endTime ?: System.currentTimeMillis()
                                val isActive = activeEntries.any { it.id == entry.id }

                                val segmentStart = max(entry.startTime, hourStart)
                                val segmentEnd = min(entryEnd, hourEnd)

                                val startFraction = (segmentStart - hourStart).toFloat() / hourDuration
                                val lengthFraction = (segmentEnd - segmentStart).toFloat() / hourDuration

                                TimeEntrySegment(
                                    activity = activity,
                                    entry = entry,
                                    isActive = isActive,
                                    startFraction = startFraction,
                                    lengthFraction = lengthFraction,
                                    hourHeightDp = 160.dp
                                )
                            }
                        }
                    }
                }
            }

            // Красная линия текущего времени
            currentTimePosition?.let { position ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .offset(y = (160.dp * position))
                ) {
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }
        }
    }

    Divider(color = Color.LightGray, thickness = 0.5.dp)
}

private fun findFreeColumn(
    entry: TimeEntryEntity,
    entryLevels: List<Pair<TimeEntryEntity, Int>>,
    hourStart: Long,
    hourEnd: Long
): Int {
    val entryEnd = entry.endTime ?: System.currentTimeMillis()
    val entryStartInHour = max(entry.startTime, hourStart)
    val entryEndInHour = min(entryEnd, hourEnd)

    // Простая стратегия: берем индекс по порядку старта
    val sortedEntries = entryLevels.map { it.first }.sortedBy { it.startTime }
    val overlappingEntries = sortedEntries.filter { other ->
        val otherEnd = other.endTime ?: System.currentTimeMillis()
        val otherStartInHour = max(other.startTime, hourStart)
        val otherEndInHour = min(otherEnd, hourEnd)

        entryStartInHour < otherEndInHour && entryEndInHour > otherStartInHour
    }

    return overlappingEntries.indexOf(entry).coerceAtLeast(0)
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
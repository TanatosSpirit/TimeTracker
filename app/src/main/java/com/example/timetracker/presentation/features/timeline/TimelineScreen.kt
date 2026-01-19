// presentation/features/timeline/TimelineScreen.kt
package com.example.timetracker.presentation.features.timeline

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timetracker.domain.model.Activity
import com.example.timetracker.domain.model.TimeEntry
import com.example.timetracker.presentation.features.timeline.TimelineViewModel
import com.example.timetracker.presentation.features.timeline.components.*
import com.example.timetracker.ui.theme.TimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val hourSegments by viewModel.hourSegments.collectAsStateWithLifecycle()
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    val activeEntries by viewModel.activeEntries.collectAsStateWithLifecycle()

    val scrollToCurrentTime by viewModel.scrollToCurrentTime.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<Pair<TimeEntry, Activity>?>(null) }

    val isDarkTheme = isSystemInDarkTheme()

    // 1. Состояние в начале TimelineScreen
    var entryToDelete by remember { mutableStateOf<Long?>(null) }

    TimeTrackerTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Time Tracker") },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Activity")
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
                    onNow = { viewModel.goToNow() }
                )

                DayTimeline(
                    hourSegments = hourSegments,
                    activities = activities,
                    activeEntries = activeEntries,
                    selectedDate = selectedDate,
                    scrollToTime = scrollToCurrentTime,
                    onScrollCompleted = { viewModel.onScrollCompleted() },
                    onEntryClick = { entry, activity ->
                        selectedEntry = Pair(entry, activity)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Диалоги и Bottom Sheets
    if (showAddDialog) {
        AddActivityDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.createAndStartActivity(name)
                showAddDialog = false
            }
        )
    }

    selectedEntry?.let { (entry, activity) ->
        ActivityBottomSheet(
            entry = entry,
            activity = activity,
            isActive = activeEntries.any { it.id == entry.id },
            onDismiss = { selectedEntry = null },
            onDeleteClick = { entryToDelete = entry.id },
            onTimeUpdate = { newStartTime, newEndTime ->
                viewModel.updateEntryTimes(entry.id, newStartTime, newEndTime)
                selectedEntry = null
            },
            onNameUpdate = { newName ->
                viewModel.updateActivityName(activity.id, newName)
            }
        )
    }

    entryToDelete?.let { entryId ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Удалить запись?") },
            text = { Text("Это действие нельзя будет отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(entryId)
                        entryToDelete = null
                        selectedEntry = null // Закрываем BottomSheet при подтверждении
                    }
                ) {
                    Text(
                        text = "Удалить",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }


}

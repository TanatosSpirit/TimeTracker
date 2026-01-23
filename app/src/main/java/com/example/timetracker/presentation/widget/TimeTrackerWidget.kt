// presentation/widget/TimeTrackerWidget.kt
package com.example.timetracker.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.timetracker.data.local.database.AppDatabase
import com.example.timetracker.data.repository.TimeTrackerRepositoryImpl
import com.example.timetracker.presentation.QuickEntryActivity
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.timetracker.presentation.MainActivity

class TimeTrackerWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getInstance(context)
        val repository = TimeTrackerRepositoryImpl(
            database.activityDao(),
            database.timeEntryDao()
        )

        val activitiesFlow = repository.getAllActivitiesByName()
        val activeEntriesFlow = repository.getActiveEntries()

        provideContent {
            val allActivities by activitiesFlow.collectAsState(initial = emptyList())
            val activeEntries by activeEntriesFlow.collectAsState(initial = emptyList())

            // Получаем текущее состояние виджета
            val prefs = currentState<Preferences>()
            val selectedColorId = prefs[longPreferencesKey("selected_color")] ?: -1L

            val activeActivityIds = activeEntries.map { it.activityId }.toSet()

            // Группируем активности по цветам
            val activityGroups = allActivities
                .map { ActivityItem(it.id, it.name, it.color) }
                .groupBy { it.color }

            WidgetContent(
                activityGroups = activityGroups,
                activeActivityIds = activeActivityIds,
                selectedColorId = selectedColorId,
                context = context
            )
        }
    }

    companion object {
        val SELECTED_COLOR_KEY = longPreferencesKey("selected_color")
    }
}

data class ActivityItem(val id: Long, val name: String, val color: Long)

@Composable
fun WidgetContent(
    activityGroups: Map<Long, List<ActivityItem>>,
    activeActivityIds: Set<Long>,
    selectedColorId: Long,
    context: Context
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .background(ColorProvider(Color.White, Color(0xFF1E1E1E)))
    ) {
        // Заголовок
        HeaderSection(
            context = context,
            selectedColorId = selectedColorId,
            activityGroups = activityGroups
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Основной контент - либо цвета, либо активности выбранного цвета
        if (selectedColorId == -1L) {
            // Показываем экран с цветами
            ColorGridScreen(
                activityGroups = activityGroups,
                activeActivityIds = activeActivityIds
            )
        } else {
            // Показываем экран с активностями конкретного цвета
            ActivityListScreen(
                activities = activityGroups[selectedColorId] ?: emptyList(),
                activeActivityIds = activeActivityIds,
                color = selectedColorId
            )
        }
    }
}

@Composable
fun HeaderSection(
    context: Context,
    selectedColorId: Long,
    activityGroups: Map<Long, List<ActivityItem>>
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // Кнопка "назад" если мы на экране активностей
        if (selectedColorId != -1L) {
            Box(
                modifier = GlanceModifier
                    .size(32.dp)
                    .background(Color(0xFF757575))
                    .cornerRadius(16.dp)
                    .clickable(
                        actionRunCallback<NavigateToColorsAction>()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    style = TextStyle(
                        color = ColorProvider(Color.White, Color.Black),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
        }

        // Заголовок
        Text(
            text = if (selectedColorId == -1L) {
                "Time Tracker"
            } else {
                val colorName = activityGroups[selectedColorId]?.firstOrNull()?.let {
                    getColorName(it.color)
                } ?: "Activities"
                colorName
            },
            style = TextStyle(
                color = ColorProvider(Color.Black, Color.White),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier
                .defaultWeight()
                .clickable(
                    onClick = actionStartActivity(
                        Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        }
                    )
                )
        )

        // Кнопка "+"
        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(Color(0xFF2196F3))
                .cornerRadius(16.dp)
                .clickable(actionStartActivity(
                    Intent(context, QuickEntryActivity::class.java)
                )),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.Black),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ColorGridScreen(
    activityGroups: Map<Long, List<ActivityItem>>,
    activeActivityIds: Set<Long>
) {
    LazyColumn(
        modifier = GlanceModifier.fillMaxSize()
    ) {
        val colorPairs = activityGroups.keys.toList().chunked(2)

        items(colorPairs) { pair ->
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Box(modifier = GlanceModifier.defaultWeight()) {
                    val colorId = pair[0]
                    val activities = activityGroups[colorId] ?: emptyList()
                    val activeCount = activities.count { it.id in activeActivityIds }

                    ColorButton(
                        colorId = colorId,
                        activityCount = activities.size,
                        activeCount = activeCount
                    )
                }

                Spacer(modifier = GlanceModifier.width(4.dp))

                Box(modifier = GlanceModifier.defaultWeight()) {
                    if (pair.size > 1) {
                        val colorId = pair[1]
                        val activities = activityGroups[colorId] ?: emptyList()
                        val activeCount = activities.count { it.id in activeActivityIds }

                        ColorButton(
                            colorId = colorId,
                            activityCount = activities.size,
                            activeCount = activeCount
                        )
                    } else {
                        Spacer(modifier = GlanceModifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))
        }
    }
}

@Composable
fun ColorButton(
    colorId: Long,
    activityCount: Int,
    activeCount: Int
) {
    val hasActiveActivity = activeCount > 0

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(colorId).copy(alpha = if (hasActiveActivity) 1f else 0.3f))
            .cornerRadius(8.dp)
            .clickable(
                actionRunCallback<SelectColorAction>(
                    actionParametersOf(
                        SelectColorAction.COLOR_ID_KEY to colorId
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = getColorName(colorId),
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.Black),
                    fontSize = 14.sp,
                    fontWeight = if (hasActiveActivity) FontWeight.Bold else FontWeight.Normal
                )
            )

            Text(
                text = "$activeCount of $activityCount ${if (activityCount == 1) "activity" else "activities"}",
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.8f), Color.Black),
                    fontSize = 11.sp
                )
            )
        }
    }
}

fun getColorName(colorValue: Long): String {
    return when (colorValue) {
        0xFFF44336L -> "Red"
        0xFFE91E63L -> "Pink"
        0xFF9C27B0L -> "Purple"
        0xFF673AB7L -> "Deep Purple"
        0xFF3F51B5L -> "Indigo"
        0xFF2196F3L -> "Blue"
        0xFF03A9F4L -> "Light Blue"
        0xFF00BCD4L -> "Cyan"
        0xFF009688L -> "Teal"
        0xFF4CAF50L -> "Green"
        0xFF8BC34AL -> "Light Green"
        0xFFCDDC39L -> "Lime"
        0xFFFFEB3BL -> "Yellow"
        0xFFFFC107L -> "Amber"
        0xFFFF9800L -> "Orange"
        0xFFFF5722L -> "Deep Orange"
        0xFF795548L -> "Brown"
        0xFF9E9E9EL -> "Grey"
        0xFF607D8BL -> "Blue Grey"
        else -> "Color"
    }
}


@Composable
fun ActivityListScreen(
    activities: List<ActivityItem>,
    activeActivityIds: Set<Long>,
    color: Long
) {
    LazyColumn(
        modifier = GlanceModifier.fillMaxSize()
    ) {
        // Группируем активности по парам для двухколоночной сетки
        val activityPairs = activities.chunked(2)

        items(activityPairs) { pair ->
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                // Левая колонка
                Box(modifier = GlanceModifier.defaultWeight()) {
                    val activity = pair[0]
                    val isActive = activity.id in activeActivityIds
                    ActivityButton(
                        activity = activity,
                        isActive = isActive
                    )
                }

                Spacer(modifier = GlanceModifier.width(4.dp))

                // Правая колонка
                Box(modifier = GlanceModifier.defaultWeight()) {
                    if (pair.size > 1) {
                        val activity = pair[1]
                        val isActive = activity.id in activeActivityIds
                        ActivityButton(
                            activity = activity,
                            isActive = isActive
                        )
                    } else {
                        Spacer(modifier = GlanceModifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))
        }
    }
}


@Composable
fun ActivityButton(
    activity: ActivityItem,
    isActive: Boolean
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(activity.color).copy(alpha = if (isActive) 1f else 0.3f))
            .cornerRadius(8.dp)
            .clickable(
                actionRunCallback<ToggleActivityAction>(
                    actionParametersOf(
                        ToggleActivityAction.ACTIVITY_ID_KEY to activity.id
                    )
                )
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = GlanceModifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = activity.name,
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.Black),
                    fontSize = 14.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}

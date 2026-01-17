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
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import com.example.timetracker.presentation.MainActivity

class TimeTrackerWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getInstance(context)
        val repository = TimeTrackerRepositoryImpl(
            database.activityDao(),
            database.timeEntryDao()
        )

        // Получаем Flow напрямую из репозитория
        val activitiesFlow = repository.getAllActivities()
        val activeEntriesFlow = repository.getActiveEntries()

        provideContent {
            // Превращаем Flow в State. Glance автоматически подпишется на изменения в Room.
            val allActivities by activitiesFlow.collectAsState(initial = emptyList())
            val activeEntries by activeEntriesFlow.collectAsState(initial = emptyList())

            val activeActivityIds = activeEntries.map { it.activityId }.toSet()

            WidgetContent(
                allActivities = allActivities.map {
                    ActivityItem(it.id, it.name, it.color) 
                },
                activeActivityIds = activeActivityIds,
                context = context
            )
        }
    }
}

data class ActivityItem(val id: Long, val name: String, val color: Long)

@Composable
fun WidgetContent(
    allActivities: List<ActivityItem>,
    activeActivityIds: Set<Long>,
    context: Context
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .background(ColorProvider(Color.White, Color(0xFF1E1E1E)))
    ) {
        // Заголовок + кнопка "+"
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "Time Tracker",
                style = TextStyle(
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

        Spacer(modifier = GlanceModifier.height(8.dp))

        LazyColumn(
            modifier = GlanceModifier
                .defaultWeight()  // Занимает оставшееся место
            // .fillMaxHeight() — если нужно жёстко
        ) {
            items(allActivities) { activity ->  // DSL: items(список) { item -> ... }
                val isActive = activity.id in activeActivityIds
                ActivityButton(
                    activity = activity,
                    isActive = isActive
                )
                // Spacer между кнопками — внутри LazyColumn (не считается ребёнком главного Column)
                Spacer(modifier = GlanceModifier.height(4.dp))
            }
        }

        // "More" button
        // Box(...) { ... }
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
            
            Text(
                text = if (isActive) "⏹" else "▶",
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.Black),
                    fontSize = 16.sp
                )
            )
        }
    }
}

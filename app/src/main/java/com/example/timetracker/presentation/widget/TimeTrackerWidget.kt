// presentation/widget/TimeTrackerWidget.kt
package com.example.timetracker.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
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
import com.example.timetracker.data.repository.TimeTrackerRepository
import com.example.timetracker.presentation.MainActivity
import com.example.timetracker.presentation.QuickEntryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import androidx.glance.action.actionParametersOf

class TimeTrackerWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getInstance(context)
        val repository = TimeTrackerRepository(
            database.activityDao(),
            database.timeEntryDao()
        )
        
        val topActivities = withContext(Dispatchers.IO) {
            repository.getTopActivities().first()
        }

        // Добавьте логирование:
        android.util.Log.d("TimeTrackerWidget", "topActivities size: ${topActivities.size}")
        topActivities.forEach { activity ->
            android.util.Log.d("TimeTrackerWidget", "Activity: id=${activity.id}, name=${activity.name}, usageCount=${activity.usageCount}, lastUsedAt=${activity.lastUsedAt}")
        }
        
        val activeEntries = withContext(Dispatchers.IO) {
            repository.getActiveEntries().first()
        }
        
        provideContent {
            WidgetContent(
                topActivities = topActivities.map { 
                    ActivityItem(it.id, it.name, it.color) 
                },
                activeActivityIds = activeEntries.map { it.activityId }.toSet(),
                context = context
            )
        }
    }
}

data class ActivityItem(val id: Long, val name: String, val color: Long)

@Composable
fun WidgetContent(
    topActivities: List<ActivityItem>,
    activeActivityIds: Set<Long>,
    context: Context
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
//            .background(Color.White)
            .background(ColorProvider(Color.White, Color(0xFF1E1E1E)))
    ) {
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
                modifier = GlanceModifier.defaultWeight()
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
        
        // Top 6 activities
        topActivities.take(6).forEach { activity ->
            val isActive = activity.id in activeActivityIds
            ActivityButton(
                activity = activity,
                isActive = isActive
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
        }
        
        // "More" button
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(36.dp)
                .background(Color(0xFFE0E0E0))
                .cornerRadius(8.dp)
                .clickable(actionStartActivity(
                    Intent(context, QuickEntryActivity::class.java)
                )),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "More...",
                style = TextStyle(fontSize = 14.sp)
            )
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

// Extension for AppDatabase singleton
//private fun AppDatabase.Companion.getInstance(context: Context): AppDatabase {
//    return androidx.room.Room.databaseBuilder(
//        context.applicationContext,
//        AppDatabase::class.java,
//        "time_tracker_database"
//    ).build()
//}

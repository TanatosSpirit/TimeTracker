// presentation/widget/ToggleActivityAction.kt
package com.example.timetracker.presentation.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.timetracker.data.local.database.AppDatabase
import com.example.timetracker.data.repository.TimeTrackerRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToggleActivityAction : ActionCallback {

    companion object {
        val ACTIVITY_ID_KEY = ActionParameters.Key<Long>("activity_id")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val activityId = parameters[ACTIVITY_ID_KEY] ?: return

        val database = AppDatabase.getInstance(context)
        val repository = TimeTrackerRepositoryImpl(
            database.activityDao(),
            database.timeEntryDao()
        )

        // Просто меняем данные в БД.
        // Flow сам "протолкнет" изменения в виджет.
        withContext(Dispatchers.IO) {
            repository.toggleActivity(activityId)
        }

        // updateAll(context) БОЛЬШЕ НЕ НУЖЕН
        TimeTrackerWidget().update(context, glanceId)
    }
}
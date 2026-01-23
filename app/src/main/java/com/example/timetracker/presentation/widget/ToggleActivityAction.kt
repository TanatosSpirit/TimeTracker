// presentation/widget/ToggleActivityAction.kt
package com.example.timetracker.presentation.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
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

        withContext(Dispatchers.IO) {
            repository.toggleActivity(activityId)
        }

        // После переключения возвращаемся на экран с цветами
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[TimeTrackerWidget.SELECTED_COLOR_KEY] = -1L
        }

        TimeTrackerWidget().update(context, glanceId)
    }
}

// Action для выбора цвета (переход к списку активностей этого цвета)
class SelectColorAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val colorId = parameters[COLOR_ID_KEY] ?: return

        // Сохраняем выбранный цвет в состоянии виджета
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[TimeTrackerWidget.SELECTED_COLOR_KEY] = colorId
        }

        // Обновляем виджет
        TimeTrackerWidget().update(context, glanceId)
    }

    companion object {
        val COLOR_ID_KEY = ActionParameters.Key<Long>("color_id")
    }
}

// Action для возврата к экрану с цветами
class NavigateToColorsAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Сбрасываем выбранный цвет
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[TimeTrackerWidget.SELECTED_COLOR_KEY] = -1L
        }

        // Обновляем виджет
        TimeTrackerWidget().update(context, glanceId)
    }
}
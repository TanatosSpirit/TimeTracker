// presentation/MainActivity.kt
package com.example.timetracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.timetracker.ui.theme.TimeTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.timetracker.presentation.features.timeline.TimelineScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeTrackerTheme  {
                TimelineScreen()
            }
        }
    }
}

// presentation/navigation/Screen.kt
package com.example.timetracker.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Calendar : Screen(
        route = "calendar",
        title = "Calendar",
        icon = Icons.Default.DateRange
    )

    object Sphere : Screen(
        route = "sphere",
        title = "Sphere",
        icon = Icons.Default.Face
    )
}
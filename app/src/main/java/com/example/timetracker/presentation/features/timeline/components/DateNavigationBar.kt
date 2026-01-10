// presentation/features/timeline/components/DateNavigationBar.kt
package com.example.timetracker.presentation.features.timeline.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetracker.presentation.common.utils.DateUtils
import java.util.Date

@Composable
fun DateNavigationBar(
    date: Date,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = DateUtils.isToday(date)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous day")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = DateUtils.formatDisplayDate(date),
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
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next day")
        }
    }
}

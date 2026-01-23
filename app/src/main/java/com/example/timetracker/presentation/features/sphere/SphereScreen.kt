// presentation/features/sphere/SphereScreen.kt
package com.example.timetracker.presentation.features.sphere

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// TODO: Заменить на реальную модель из domain слоя
data class ActivityGroup(
    val color: Color,
    val colorName: String,
    val activities: List<ActivityItem>
)

data class ActivityItem(
    val id: Long,
    val name: String,
    val color: Color,
    val totalTime: Long // в миллисекундах
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SphereScreen() {
    // TODO: Получить данные из ViewModel
    val activityGroups = getStubData()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Sphere") },
            windowInsets = WindowInsets(0, 0, 0, 0),
        )

        if (activityGroups.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет данных",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(activityGroups) { group ->
                    ColorGroupSection(group)
                }
            }
        }
    }
}

@Composable
fun ColorGroupSection(group: ActivityGroup) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Заголовок группы с цветом
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(group.color)
            )

            Text(
                text = group.colorName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Список активностей в этой группе
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 44.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            group.activities.forEach { activity ->
                ActivityCard(activity)
            }
        }
    }
}

@Composable
fun ActivityCard(activity: ActivityItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = formatTime(activity.totalTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Вспомогательная функция для форматирования времени
fun formatTime(millis: Long): String {
    val hours = millis / (1000 * 60 * 60)
    val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)

    return when {
        hours > 0 -> "${hours}ч ${minutes}м"
        minutes > 0 -> "${minutes}м"
        else -> "< 1м"
    }
}

// TODO: Удалить после интеграции с ViewModel
// Заглушка данных для визуализации
fun getStubData(): List<ActivityGroup> {
    return listOf(
        ActivityGroup(
            color = Color(0xFFE57373),
            colorName = "Красный",
            activities = listOf(
                ActivityItem(1, "Работа", Color(0xFFE57373), 7200000), // 2 часа
                ActivityItem(2, "Встречи", Color(0xFFE57373), 3600000) // 1 час
            )
        ),
        ActivityGroup(
            color = Color(0xFF64B5F6),
            colorName = "Синий",
            activities = listOf(
                ActivityItem(3, "Учеба", Color(0xFF64B5F6), 5400000), // 1.5 часа
                ActivityItem(4, "Чтение", Color(0xFF64B5F6), 1800000) // 30 минут
            )
        ),
        ActivityGroup(
            color = Color(0xFF81C784),
            colorName = "Зеленый",
            activities = listOf(
                ActivityItem(5, "Спорт", Color(0xFF81C784), 2700000), // 45 минут
                ActivityItem(6, "Прогулка", Color(0xFF81C784), 1200000) // 20 минут
            )
        )
    )
}
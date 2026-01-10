// domain/model/Activity.kt
package com.example.timetracker.domain.model

/**
 * Domain модель активности.
 * Не зависит от Room, SQLite или других деталей реализации.
 */
data class Activity(
    val id: Long,
    val name: String,
    val color: Long,
    val usageCount: Int = 0,
    val lastUsedAt: Long? = null  // null = никогда не использовалась
) {
    companion object {
        /**
         * Палитра цветов для активностей
         */
        val COLOR_PALETTE = listOf(
            0xFFEF5350, 0xFFEC407A, 0xFFAB47BC, 0xFF7E57C2,
            0xFF5C6BC0, 0xFF42A5F5, 0xFF29B6F6, 0xFF26C6DA,
            0xFF26A69A, 0xFF66BB6A, 0xFF9CCC65, 0xFFD4E157,
            0xFFFFEE58, 0xFFFFCA28, 0xFFFF7043, 0xFF8D6E63
        )
    }
}
// data/local/migrations/Migrations.kt
package com.example.timetracker.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Создаем новую таблицу spheres
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS spheres (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                color INTEGER NOT NULL,
                sphere TEXT
            )
        """)

        db.execSQL("""
            CREATE UNIQUE INDEX index_spheres_color_sphere 
            ON spheres(color, sphere)
        """)

        //2. Переносим существующие цвета в таблицу spheres
        db.execSQL("""
            INSERT INTO spheres (color, sphere)
            SELECT DISTINCT color, NULL FROM activities
        """)

        // 3.Создаем временную таблицу с новой структурой
        db.execSQL("""
            CREATE TABLE activities_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                sphereId INTEGER NOT NULL,
                usageCount INTEGER NOT NULL,
                lastUsedAt INTEGER NOT NULL,
                FOREIGN KEY(sphereId) REFERENCES spheres(id) ON DELETE RESTRICT
            )
        """)

        // Копируем данные, связывая со старыми цветами
        db.execSQL("""
            INSERT INTO activities_new (id, name, sphereId, usageCount, lastUsedAt)
            SELECT a.id, a.name, s.id, a.usageCount, a.lastUsedAt
            FROM activities a
            INNER JOIN spheres s ON a.color = s.color
        """)

        // Удаляем старую таблицу
        db.execSQL("DROP TABLE activities")

        // Переименовываем новую таблицу
        db.execSQL("ALTER TABLE activities_new RENAME TO activities")

        // Создаем индекс для внешнего ключа
        db.execSQL("CREATE INDEX index_activities_sphereId ON activities(sphereId)")
    }
}
// data/local/database/AppDatabase.kt
package com.example.timetracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timetracker.data.local.MIGRATION_1_2
import com.example.timetracker.data.local.dao.ActivityDao
import com.example.timetracker.data.local.dao.TimeEntryDao
import com.example.timetracker.data.local.dao.SphereDao
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity
import com.example.timetracker.data.local.entities.SphereEntity

@Database(
    entities = [ActivityEntity::class, TimeEntryEntity::class, SphereEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun timeEntryDao(): TimeEntryDao
    abstract fun sphereDao(): SphereDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "time_tracker_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
// data/local/database/AppDatabase.kt
package com.example.timetracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timetracker.data.local.dao.ActivityDao
import com.example.timetracker.data.local.dao.TimeEntryDao
import com.example.timetracker.data.local.entities.ActivityEntity
import com.example.timetracker.data.local.entities.TimeEntryEntity

@Database(
    entities = [ActivityEntity::class, TimeEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun timeEntryDao(): TimeEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "time_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
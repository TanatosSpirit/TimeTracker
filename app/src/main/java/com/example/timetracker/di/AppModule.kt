// di/AppModule.kt
package com.example.timetracker.di

import android.content.Context
import androidx.room.Room
import com.example.timetracker.data.local.dao.ActivityDao
import com.example.timetracker.data.local.dao.TimeEntryDao
import com.example.timetracker.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "time_tracker_database"
//        ).build()
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    fun provideActivityDao(database: AppDatabase): ActivityDao {
        return database.activityDao()
    }
    
    @Provides
    fun provideTimeEntryDao(database: AppDatabase): TimeEntryDao {
        return database.timeEntryDao()
    }
}

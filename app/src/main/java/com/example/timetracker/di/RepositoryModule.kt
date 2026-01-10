// di/RepositoryModule.kt
package com.example.timetracker.di

import com.example.timetracker.data.repository.TimeTrackerRepositoryImpl
import com.example.timetracker.domain.repository.ITimeTrackerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль для предоставления репозитория
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTimeTrackerRepository(
        impl: TimeTrackerRepositoryImpl
    ): ITimeTrackerRepository
}
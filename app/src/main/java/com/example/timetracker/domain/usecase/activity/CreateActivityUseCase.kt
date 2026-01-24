//// domain/usecase/activity/CreateActivityUseCase.kt
//package com.example.timetracker.domain.usecase.activity
//
//import com.example.timetracker.domain.model.Activity
//import com.example.timetracker.domain.repository.ITimeTrackerRepository
//import javax.inject.Inject
//import kotlin.random.Random
//
///**
// * Use Case для создания новой активности
// */
//class CreateActivityUseCase @Inject constructor(
//    private val repository: ITimeTrackerRepository
//) {
//    /**
//     * Создаёт новую активность со случайным цветом
//     *
//     * @param name название активности
//     * @param timestamp время создания
//     * @return ID созданной активности
//     */
//    suspend operator fun invoke(name: String, timestamp: Long): Long {
//        val color = DEFAULT_COLOR
//        return repository.createActivity(
//            name = name,
//            color = color,
//            timestamp = timestamp
//        )
//    }
//
//    companion object {
//        private const val DEFAULT_COLOR = 0xFF9E9E9E // Серый цвет
//    }
//}
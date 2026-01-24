//package com.example.timetracker.domain.usecase.activity
//
//import com.example.timetracker.domain.repository.ITimeTrackerRepository
//import javax.inject.Inject
//
//class UpdateActivityColor @Inject constructor(
//    private val repository: ITimeTrackerRepository
//) {
//    suspend operator fun invoke(activityId: Long, color: Long) {
//        repository.updateActivityColor(activityId, color)
//    }
//}
package com.uqac.actilink.repository

import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log

class ActivityRepository {
    private val firebaseService = FirebaseService()

    private val _joinedActivities = MutableStateFlow<List<ActivityModel>>(emptyList())
    val joinedActivities: StateFlow<List<ActivityModel>> = _joinedActivities

    suspend fun loadJoinedActivities(userId: String) {
        val activities = firebaseService.getJoinedActivities(userId)
        _joinedActivities.value = activities
    }

    suspend fun joinActivity(activityId: String, userId: String): Result<Unit> {
        val result = firebaseService.joinActivity(activityId)
        if (result.isSuccess) {
            loadJoinedActivities(userId)
        }
        return result
    }

    suspend fun leaveActivity(activityId: String, userId: String): Result<Unit> {
        val result = firebaseService.leaveActivity(activityId)
        if (result.isSuccess) {
            loadJoinedActivities(userId)
        }
        return result
    }

    suspend fun addActivity(activity: ActivityModel) {
        val success = firebaseService.addActivity(activity)
        if (success) {
            loadJoinedActivities(activity.creatorId) // MàJ si créateur
        }
    }
}
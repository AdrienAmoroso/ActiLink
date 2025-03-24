package com.uqac.actilink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.services.FirebaseService
import java.util.UUID

class ActivityViewModel(private val repository: FirebaseService) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityModel>>(emptyList())
    val activities: StateFlow<List<ActivityModel>> = _activities

    init {
        loadActivities()
    }

    // Charger les activités
    fun loadActivities() {
        viewModelScope.launch {
            _activities.value = repository.getActivities()
        }
    }

    // Ajouter une activité
    fun addActivity(title: String, date: String, location: String, userId: String) {
        val activity = ActivityModel(
            id = UUID.randomUUID().toString(),
            title = title,
            dateTime = date,
            location = location,
            creatorId = userId
        )

        viewModelScope.launch {
            val success = repository.addActivity(activity)
            if (success) {
                loadActivities()
            }
        }
    }
}
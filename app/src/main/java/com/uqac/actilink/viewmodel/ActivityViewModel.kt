// viewmodel/ActivityViewModel.kt
package com.uqac.actilink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.repository.ActivityRepository
import com.uqac.actilink.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.UUID

class ActivityViewModel(
    private val activityRepository: ActivityRepository = ActivityRepository()
) : ViewModel() {

    private val repository = FirebaseService()

    // Liste brute des activités (telles que récupérées de Firestore)
    private val _activities = MutableStateFlow<List<ActivityModel>>(emptyList())
    val activities: StateFlow<List<ActivityModel>> = _activities

    // Champ de recherche textuel (filtrage par titre, lieu ou type)
    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText

    /**
     * Combine la liste brute + le champ de recherche pour produire une liste filtrée.
     * Filtre sur title, location, type.
     */
    private val _filteredActivities: StateFlow<List<ActivityModel>> =
        combine(_activities, _filterText) { all, text ->
            val q = text.trim().lowercase()
            if (q.isBlank()) all
            else all.filter { a ->
                a.title.lowercase().contains(q) ||
                        a.location.lowercase().contains(q) ||
                        a.type.lowercase().contains(q)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    val filteredActivities: StateFlow<List<ActivityModel>> = _filteredActivities

    // Activités auxquelles l'utilisateur a déjà participé
    val joinedActivities: StateFlow<List<ActivityModel>> = activityRepository.joinedActivities

    init {
        loadActivities()
    }

    /** Charge toutes les activités depuis Firestore. */
    fun loadActivities() {
        viewModelScope.launch {
            _activities.value = repository.getActivities()
        }
    }

    /** Met à jour le texte de filtre. */
    fun updateFilter(newFilter: String) {
        _filterText.value = newFilter
    }

    /** Rejoint une activité, puis recharge la liste. */
    fun joinActivity(activityId: String) {
        val userId = repository.getUserId() ?: return
        viewModelScope.launch {
            activityRepository.joinActivity(activityId, userId)
            loadActivities()
        }
    }

    /** Quitte une activité, puis recharge la liste. */
    fun leaveActivity(activityId: String) {
        val userId = repository.getUserId() ?: return
        viewModelScope.launch {
            activityRepository.leaveActivity(activityId, userId)
            loadActivities()
        }
    }

    /**
     * Ajoute une nouvelle activité.
     * @param type facultatif
     */
    fun addActivity(
        title: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        userId: String,
        latitude: Double,
        longitude: Double,
        type: String = ""
    ) {
        val activity = ActivityModel(
            id = UUID.randomUUID().toString(),
            title = title,
            type = type,
            dateTime = date,
            startTime = startTime,
            endTime = endTime,
            location = location,
            creatorId = userId,
            participants = emptyList(),
            latitude = latitude,
            longitude = longitude
        )
        viewModelScope.launch {
            activityRepository.addActivity(activity)
            loadActivities()
        }
    }

    /**
     * Met à jour une activité existante.
     * Firestore 'set' écrase le document existant avec même id.
     */
    fun updateActivity(activity: ActivityModel) {
        viewModelScope.launch {
            activityRepository.addActivity(activity)
            loadActivities()
        }
    }

    /** Supprime une activité par son id. */
    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            val result = repository.deleteActivity(activityId)
            result.onSuccess {
                loadActivities()
            }.onFailure { /* gérer l’erreur si besoin */ }
        }
    }
}

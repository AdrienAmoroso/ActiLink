package com.uqac.actilink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.services.FirebaseService
import java.util.UUID

class ActivityViewModel : ViewModel() {

    private val repository = FirebaseService()

    // Liste brute des activités (telles que récupérées de Firestore)
    private val _activities = MutableStateFlow<List<ActivityModel>>(emptyList())
    val activities: StateFlow<List<ActivityModel>> = _activities

    // Champ de recherche textuel (filtrage par titre, lieu ou type)
    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText

    /**
     * On combine la liste brute + le champ de recherche pour produire une liste filtrée.
     * - On filtre sur title, location ou type, en ignorant la casse.
     */
    private val _filteredActivities: StateFlow<List<ActivityModel>> =
        combine(_activities, _filterText) { allActivities, text ->
            val query = text.trim().lowercase() // recherche en minuscules
            if (query.isBlank()) {
                // Pas de filtre => renvoie la liste brute
                allActivities
            } else {
                // Filtre sur titre, lieu ou type
                allActivities.filter { activity ->
                    val titleMatch = activity.title.lowercase().contains(query)
                    val locationMatch = activity.location.lowercase().contains(query)
                    val typeMatch = activity.type.lowercase().contains(query)
                    titleMatch || locationMatch || typeMatch
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    /**
     * Liste à afficher (déjà filtrée).
     * Tu peux utiliser directement `filteredActivities` dans l'UI.
     */
    val filteredActivities: StateFlow<List<ActivityModel>> = _filteredActivities

    init {
        loadActivities()
    }

    fun joinActivity(activityId: String) {
        viewModelScope.launch {
            val result = repository.joinActivity(activityId)
            result.onSuccess {
                println("Utilisateur ajouté à l'activité !")
                loadActivities()
            }.onFailure { error ->
                println("Erreur : ${error.message}")
            }
        }
    }

    fun leaveActivity(activityId: String) {
        viewModelScope.launch {
            val result = repository.leaveActivity(activityId)

            result.onSuccess {
                println("Utilisateur retiré de l'activité !")

                // Rafraîchir la liste après modification
                loadActivities()
            }.onFailure { error ->
                println("Erreur : ${error.message}")
            }
        }
    }

    // Charger les activités depuis Firestore
    fun loadActivities() {
        viewModelScope.launch {
            _activities.value = repository.getActivities()
        }
    }

    // Mettre à jour le texte du filtre
    fun updateFilter(newFilter: String) {
        _filterText.value = newFilter
    }

    // Ajouter une activité
    fun addActivity(
        title: String,
        date: String,
        location: String,
        userId: String,
        latitude: Double,
        longitude: Double,
        type: String = ""   // si tu veux renseigner le type
    ) {
        val activity = ActivityModel(
            id = UUID.randomUUID().toString(),
            title = title,
            dateTime = date,
            location = location,
            creatorId = userId,
            latitude = latitude,
            longitude = longitude,
            type = type
        )

        viewModelScope.launch {
            val success = repository.addActivity(activity)
            if (success) {
                loadActivities()
            }
        }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            val result = repository.deleteActivity(activityId)

            result.onSuccess {
                println("Activité supprimée avec succès !")

                // Rafraîchir la liste après suppression
                loadActivities()
            }.onFailure { error ->
                println("Erreur : ${error.message}")
            }
        }
    }

    // Fonction pour peuplé la bdd
    /*
    fun populateChicoutimi() {
        viewModelScope.launch {
            val success = repository.populateChicoutimiActivities()
            if (success) {
                // Ensuite, recharge la liste
                loadActivities()
            }
        }
    }*/
}

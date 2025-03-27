package com.uqac.actilink.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.uqac.actilink.models.ActivityModel

// Data class pour les marqueurs enrichis
data class ActivityMarker(
    val position: LatLng,
    val title: String,
    val snippet: String
)

class MapViewModel : ViewModel() {

    // Position de la caméra
    private val _cameraPosition = MutableStateFlow<LatLng?>(null)
    val cameraPosition: StateFlow<LatLng?> = _cameraPosition.asStateFlow()

    fun updateCameraPosition(newLatLng: LatLng) {
        _cameraPosition.value = newLatLng
    }

    // Liste des marqueurs enrichis
    private val _markers = MutableStateFlow<List<ActivityMarker>>(emptyList())
    val markers: StateFlow<List<ActivityMarker>> = _markers.asStateFlow()

    fun addMarker(marker: ActivityMarker) {
        _markers.value = _markers.value + marker
    }

    fun removeMarker(marker: ActivityMarker) {
        _markers.value = _markers.value.filter { it != marker }
    }

    // Mettre à jour les marqueurs à partir d'une liste d'activités
    fun updateMarkersFromActivities(activities: List<ActivityModel>) {
        _markers.value = activities.map { activity ->
            ActivityMarker(
                position = LatLng(activity.latitude, activity.longitude),
                title = activity.title,
                snippet = "Date: ${activity.dateTime} - Lieu: ${activity.location}"
            )
        }
    }

    // Récupérer la localisation de l'utilisateur
    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                updateCameraPosition(userLatLng)
            } else {
                val defaultLocation = LatLng(48.8584, 2.2945)
                updateCameraPosition(defaultLocation)
            }
        }
    }
}

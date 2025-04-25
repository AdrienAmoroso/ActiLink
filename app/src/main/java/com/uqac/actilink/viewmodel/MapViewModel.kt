// viewmodel/MapViewModel.kt
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

/**
 * Data class pour représenter un marqueur enrichi
 * avec position, titre et snippet (affiché dans l’infobulle).
 */
data class ActivityMarker(
    val position: LatLng,
    val title: String,
    val snippet: String
)

class MapViewModel : ViewModel() {

    // Position courante de la caméra / utilisateur
    private val _cameraPosition = MutableStateFlow<LatLng?>(null)
    val cameraPosition: StateFlow<LatLng?> = _cameraPosition.asStateFlow()

    fun updateCameraPosition(newLatLng: LatLng) {
        _cameraPosition.value = newLatLng
    }

    // Liste des marqueurs à afficher
    private val _markers = MutableStateFlow<List<ActivityMarker>>(emptyList())
    val markers: StateFlow<List<ActivityMarker>> = _markers.asStateFlow()

    /**
     * Met à jour les marqueurs à partir des activités.
     * On inclut la date et l’heure formatée ("10h30-15h00") dans le snippet.
     */
    fun updateMarkersFromActivities(activities: List<ActivityModel>) {
        _markers.value = activities.map { activity ->
            // format "HH:mm" → "H'h'mm"
            val timeText = activity.startTime.replace(":", "h") +
                    "-" +
                    activity.endTime.replace(":", "h")

            ActivityMarker(
                position = LatLng(activity.latitude, activity.longitude),
                title    = activity.title,
                snippet  = "Date: ${activity.dateTime} • Heure: $timeText"
            )
        }
    }

    // Récupère la dernière localisation connue
    @SuppressLint("MissingPermission")
    fun getUserLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
            val latLng = loc?.let { LatLng(it.latitude, it.longitude) }
                ?: LatLng(48.8584, 2.2945) // fallback à Paris
            updateCameraPosition(latLng)
        }
    }

    // Rayon d’affichage autorisé (en km)
    private val _allowedDistance = MutableStateFlow(10f)
    val allowedDistance: StateFlow<Float> = _allowedDistance.asStateFlow()

    fun setAllowedDistance(distance: Float) {
        _allowedDistance.value = distance
    }

    private val _goToActivityList = MutableStateFlow(false)
    val goToActivityList: StateFlow<Boolean> = _goToActivityList

    fun navigateToActivityList() {
        _goToActivityList.value = true
    }

    fun resetNavigationFlag() {
        _goToActivityList.value = false
    }

}

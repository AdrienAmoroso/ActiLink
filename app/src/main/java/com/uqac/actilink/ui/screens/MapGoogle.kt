package com.uqac.actilink.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uqac.actilink.viewmodel.ActivityViewModel
import com.uqac.actilink.viewmodel.MapViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Circle
import kotlin.math.*

@SuppressLint("MissingPermission")
@Composable
fun MapGoogle(mapViewModel: MapViewModel, activityViewModel: ActivityViewModel) {
    val currentCameraPosition by mapViewModel.cameraPosition.collectAsState()
    val markersList by mapViewModel.markers.collectAsState()
    val activities by activityViewModel.activities.collectAsState()
    // Rayon autorisé en km (paramétré dans Settings)
    val allowedDistance by mapViewModel.allowedDistance.collectAsState()

    val mapUiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }

    // Mise à jour des markers en fonction des activités
    mapViewModel.updateMarkersFromActivities(activities)

    // État pour la recherche (facultatif)
    var searchQuery by remember { mutableStateOf("") }

    // Fonction locale pour calculer la distance en mètres entre deux points
    fun calculateDistanceMeters(from: com.google.android.gms.maps.model.LatLng, to: com.google.android.gms.maps.model.LatLng): Double {
        val earthRadius = 6371000.0 // en mètres
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLng = Math.toRadians(to.longitude - from.longitude)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
                sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    if (currentCameraPosition != null) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentCameraPosition!!, 10f)
        }

        // Filtrer les markers en fonction du rayon autorisé et de la recherche
        val filteredMarkers = markersList.filter { marker ->
            // Calcule la distance entre la position de l'utilisateur et celle du marker
            val distance = calculateDistanceMeters(currentCameraPosition!!, marker.position)
            distance <= allowedDistance * 1000 // conversion km → m
        }.filter { marker ->
            // Filtrage facultatif sur le titre via la barre de recherche
            searchQuery.isEmpty() || marker.title.contains(searchQuery, ignoreCase = true)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = mapUiSettings,
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                // Affichage du cercle représentant la zone autorisée
                Circle(
                    center = currentCameraPosition!!,
                    radius = (allowedDistance * 1000).toDouble(), // rayon en mètres
                    fillColor = Color(0x220000FF),
                    strokeColor = Color.Blue,
                    strokeWidth = 2f
                )
                // Affichage des markers filtrés
                filteredMarkers.forEach { marker ->
                    Marker(
                        state = MarkerState(position = marker.position),
                        title = marker.title,
                        snippet = marker.snippet
                    )
                }
            }
            // Exemple de barre de recherche (optionnelle)
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un marker") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}



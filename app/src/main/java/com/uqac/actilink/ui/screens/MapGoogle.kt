// ui/screens/MapGoogle.kt
package com.uqac.actilink.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uqac.actilink.viewmodel.ActivityViewModel
import com.uqac.actilink.viewmodel.MapViewModel
import kotlin.math.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.google.maps.android.compose.*





@SuppressLint("MissingPermission")
@Composable
fun MapGoogle(
    mapViewModel: MapViewModel,
    activityViewModel: ActivityViewModel
) {
    val currentCameraPosition by mapViewModel.cameraPosition.collectAsState()
    val markersList           by mapViewModel.markers.collectAsState()
    val activities            by activityViewModel.activities.collectAsState()
    val allowedDistance       by mapViewModel.allowedDistance.collectAsState()

    LaunchedEffect(activities) {
        mapViewModel.updateMarkersFromActivities(activities)
    }

    var searchQuery by remember { mutableStateOf("") }
    val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }

    if (currentCameraPosition != null) {
        val cameraState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentCameraPosition!!, 10f)
        }

        val filtered = markersList
            .filter { distanceBetween(currentCameraPosition!!, it.position) <= allowedDistance * 1000 }
            .filter { it.title.contains(searchQuery, ignoreCase = true) }

        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier            = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                uiSettings          = uiSettings,
                properties          = MapProperties(isMyLocationEnabled = true)
            ) {
                Circle(
                    center = currentCameraPosition!!,
                    radius = (allowedDistance * 1000).toDouble(),
                    fillColor = Color(0x220000FF),
                    strokeColor = Color.Blue,
                    strokeWidth = 2f
                )

                filtered.forEach { marker ->
                    val matchedActivity = activities.find {
                        it.title == marker.title && marker.snippet.contains(it.dateTime)
                    }

                    matchedActivity?.let { activity ->
                        MarkerInfoWindow(
                            state = MarkerState(position = marker.position),
                            onInfoWindowClick = {
                                activityViewModel.selectActivity(activity)
                                mapViewModel.navigateToActivityList()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surface, // Couleur de fond
                                        shape = RoundedCornerShape(16.dp) // Bords arrondis
                                    )
                                    .padding(8.dp) // Espacement interne
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    Text(marker.title, style = MaterialTheme.typography.titleMedium)
                                    Text(marker.snippet)
                                    Spacer(Modifier.height(4.dp))
                                    Button(onClick = {
                                        activityViewModel.selectActivity(activity)
                                        mapViewModel.navigateToActivityList()
                                    }) {
                                        Text("Voir")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TextField(
                value         = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder   = { Text("Rechercher…") },
                singleLine    = true,
                shape         = RoundedCornerShape(8.dp),
                colors        = TextFieldDefaults.colors(
                    focusedContainerColor    = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    unfocusedContainerColor  = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    focusedIndicatorColor    = Color.Transparent,
                    unfocusedIndicatorColor  = Color.Transparent,
                    disabledIndicatorColor   = Color.Transparent,
                    cursorColor              = MaterialTheme.colorScheme.primary
                ),
                modifier      = Modifier
                    .fillMaxWidth(0.75f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}

private fun distanceBetween(a: LatLng, b: LatLng): Double {
    val R = 6_371_000.0
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val u = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(a.latitude)) *
            cos(Math.toRadians(b.latitude)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(u), sqrt(1 - u))
    return R * c
}

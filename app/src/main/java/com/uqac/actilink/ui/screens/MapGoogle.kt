package com.uqac.actilink.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uqac.actilink.viewmodel.MapViewModel

@SuppressLint("MissingPermission")
@Composable
fun MapGoogle(mapViewModel: MapViewModel) {
    val currentCameraPosition by mapViewModel.cameraPosition.collectAsState()
    val markersList by mapViewModel.markers.collectAsState()
    var mapUiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }

    if (currentCameraPosition != null) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentCameraPosition!!, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            markersList.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.position),
                    title = marker.title,
                    snippet = marker.snippet
                )
            }
        }
    }
}

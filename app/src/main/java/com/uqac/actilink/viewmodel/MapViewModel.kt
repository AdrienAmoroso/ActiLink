package com.uqac.actilink.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    // StateFlow to hold the current camera position (location and zoom)
    private val _cameraPosition = MutableStateFlow<LatLng?>(null)
    val cameraPosition: StateFlow<LatLng?> = _cameraPosition.asStateFlow()

    // Function to update the camera position
    fun updateCameraPosition(newLatLng: LatLng) {
        _cameraPosition.value = newLatLng
    }

    // Example : StateFlow to manage markers on the map
    private val _markers = MutableStateFlow<List<LatLng>>(emptyList())
    val markers: StateFlow<List<LatLng>> = _markers.asStateFlow()

    fun addMarker(latLng: LatLng) {
        _markers.value = _markers.value + latLng
    }

    fun removeMarker(latLng: LatLng) {
        _markers.value = _markers.value.filter { it != latLng }
    }

    // Function to get the user's location
    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    updateCameraPosition(userLatLng)
                } else {
                    // Handle the case where the location is null
                    val defaultLocation = LatLng(48.8584, 2.2945) // Paris as a default
                    updateCameraPosition(defaultLocation)
                }
            }
    }
}
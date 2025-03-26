package com.uqac.actilink

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uqac.actilink.ui.screens.MapGoogle
import com.uqac.actilink.ui.theme.ActiLinkTheme
import com.uqac.actilink.viewmodel.MapViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.uqac.actilink.ui.screens.AuthScreen
import com.uqac.actilink.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapViewModel: MapViewModel by viewModels()
        val authViewModel: AuthViewModel by viewModels()

        setContent {
            ActiLinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isUserLoggedIn by remember { mutableStateOf<FirebaseUser?>(null) }
                    var permissionGranted by remember { mutableStateOf(false) }
                    // Permission launcher
                    val launcher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        permissionGranted = isGranted
                    }

                    // Check user's connection status
                    LaunchedEffect(key1 = auth) {
                        auth.addAuthStateListener { firebaseAuth ->
                            isUserLoggedIn = firebaseAuth.currentUser
                        }
                    }
                    // Check the permission when user is logged in
                    LaunchedEffect(key1 = isUserLoggedIn) {
                        if (isUserLoggedIn != null) {
                            when {
                                ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    // Permission already granted
                                    permissionGranted = true
                                }

                                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                                    // Show explanation
                                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }

                                else -> {
                                    // Request the permission
                                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        }
                    }
                    // Display the good screen and get user location
                    if (isUserLoggedIn != null && permissionGranted) {
                        mapViewModel.getUserLocation(this@MainActivity, fusedLocationClient)
                        MapGoogle(mapViewModel = mapViewModel)
                    } else {
                        AuthScreen(authViewModel)
                    }
                }
            }
        }
    }
}
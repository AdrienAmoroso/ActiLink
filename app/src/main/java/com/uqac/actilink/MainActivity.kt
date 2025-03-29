package com.uqac.actilink

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uqac.actilink.ui.screens.ActivityScreen
import com.uqac.actilink.ui.screens.AuthScreen
import com.uqac.actilink.ui.screens.MapGoogle
import com.uqac.actilink.ui.theme.ActiLinkTheme
import com.uqac.actilink.viewmodel.ActivityViewModel
import com.uqac.actilink.viewmodel.AuthViewModel
import com.uqac.actilink.viewmodel.MapViewModel

enum class Screen {
    Home, Activity, Settings, Auth
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapViewModel: MapViewModel by viewModels()
        val authViewModel: AuthViewModel by viewModels()
        val activityViewModel: ActivityViewModel by viewModels()

        setContent {
            ActiLinkTheme {
                var currentUser by remember { mutableStateOf<FirebaseUser?>(null) }
                var permissionGranted by remember { mutableStateOf(false) }
                var selectedScreen by remember { mutableStateOf<Screen>(Screen.Auth) }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    permissionGranted = isGranted
                }

                LaunchedEffect(auth) {
                    auth.addAuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser
                        if (firebaseAuth.currentUser != null && selectedScreen == Screen.Auth) {
                            selectedScreen = Screen.Home
                        }
                    }
                }

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        when {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                permissionGranted = true
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            else -> {
                                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    }
                }

                Scaffold(
                    bottomBar = {
                        BottomMenuBar(
                            selectedScreen = selectedScreen,
                            onScreenSelected = { selectedScreen = it }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (selectedScreen) {
                            Screen.Home -> {
                                if (currentUser != null) {
                                    val activities by activityViewModel.activities.collectAsState()
                                    // Met à jour les marqueurs en fonction des activités
                                    LaunchedEffect(activities) {
                                        mapViewModel.updateMarkersFromActivities(activities)
                                    }
                                    mapViewModel.getUserLocation(this@MainActivity, fusedLocationClient)
                                    MapGoogle(mapViewModel = mapViewModel)
                                } else {
                                    AuthScreen(viewModel = authViewModel)
                                }
                            }
                            Screen.Activity -> {
                                if (currentUser != null) {
                                    ActivityScreen(viewModel = activityViewModel)
                                } else {
                                    AuthScreen(viewModel = authViewModel)
                                }
                            }
                            Screen.Settings -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Settings Screen", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                            Screen.Auth -> {
                                AuthScreen(viewModel = authViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

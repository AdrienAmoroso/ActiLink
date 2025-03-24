package com.uqac.actilink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.uqac.actilink.ui.theme.ActiLinkTheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import android.location.Location
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.padding



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActiLinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var permissionGranted by remember { mutableStateOf(false) }

                    val launcher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        permissionGranted = isGranted
                    }

                    // Demander la permission au lancement de l'application
                    LaunchedEffect(key1 = true) {
                        when {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                // Permission déjà accordée
                                permissionGranted = true
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                                // Afficher une explication (facultatif)
                                // TODO: Afficher une explication à l'utilisateur
                                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            else -> {
                                // Demander la permission
                                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    }

                    // Si la permission est accordée, afficher la main screen
                    if (permissionGranted) {
                        MainScreen()
                    } else {
                        // Gérer le cas où la permission n'est pas accordée
                        // TODO: Afficher un message à l'utilisateur
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ActiLinkTheme {
        Greeting("Android")
    }
}

@Composable
fun MyMap(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var mapUiSettings by remember {
        mutableStateOf(MapUiSettings(compassEnabled = false))
    }
    var mapProperties by remember {
        mutableStateOf(MapProperties(maxZoomPreference = 18f, minZoomPreference = 5f))
    }

    // Obtenir la dernière position connue
    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
        }
    }

    // Définir la position de la caméra
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: LatLng(0.0,0.0), 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties
    )
}

sealed class Screen(val route: String, val title: String) {
    object Map : Screen("map", "Carte")
    object Profile : Screen("profile", "Profil")
}

@Composable
fun BottomNavigationBar(currentScreen: String, onItemSelected: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == Screen.Map.route,
            onClick = { onItemSelected(Screen.Map) },
            label = { Text(Screen.Map.title) },
            icon = { Icon(Icons.Default.Place, contentDescription = null) }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.Profile.route,
            onClick = { onItemSelected(Screen.Profile) },
            label = { Text(Screen.Profile.title) },
            icon = { Icon(Icons.Default.Person, contentDescription = null) }
        )
    }
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Map) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = selectedScreen.route,
                onItemSelected = { selectedScreen = it }
            )
        }
    ) { paddingValues ->
        when (selectedScreen) {
            is Screen.Map -> MyMap(modifier = Modifier.padding(paddingValues))
            is Screen.Profile -> GreetingPreview()
        }
    }
}


// MainActivity.kt
package com.uqac.actilink

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uqac.actilink.ui.screens.AddActivityScreen
import com.uqac.actilink.ui.screens.ActivityListScreen
import com.uqac.actilink.ui.screens.AuthScreen
import com.uqac.actilink.ui.screens.EditActivityScreen
import com.uqac.actilink.ui.screens.MapGoogle
import com.uqac.actilink.ui.screens.SettingsScreen
import com.uqac.actilink.ui.screens.SignUpScreen
import com.uqac.actilink.ui.screens.BottomMenuBar
import com.uqac.actilink.ui.theme.ActiLinkTheme
import com.uqac.actilink.viewmodel.ActivityViewModel
import com.uqac.actilink.viewmodel.AuthViewModel
import com.uqac.actilink.viewmodel.MapViewModel

enum class Screen {
    Auth, SignUp, Home, ActivityList, AddActivity, EditActivity, Settings
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val authVM: AuthViewModel by viewModels()
        val mapVM : MapViewModel by viewModels()
        val actVM : ActivityViewModel by viewModels()

        setContent {
            ActiLinkTheme {
                // On observe la connexion Firebase
                val currentUser by remember { mutableStateOf(auth.currentUser) }
                var selectedScreen by remember { mutableStateOf(
                    if (currentUser != null) Screen.Home else Screen.Auth
                ) }

                // Chaque fois que l’état FirebaseAuth change, on adapte l’écran
                LaunchedEffect(auth) {
                    auth.addAuthStateListener {
                        selectedScreen = if (it.currentUser != null) Screen.Home else Screen.Auth
                    }
                }

                Scaffold(
                    bottomBar = {
                        BottomMenuBar(
                            selectedScreen   = selectedScreen,
                            onScreenSelected = { selectedScreen = it },
                            isAuthenticated  = auth.currentUser != null
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color    = MaterialTheme.colorScheme.background
                    ) {
                        when (selectedScreen) {
                            Screen.Auth -> AuthScreen(
                                viewModel      = authVM,
                                onSignUpClick  = { selectedScreen = Screen.SignUp },
                                onLoginSuccess = { selectedScreen = Screen.Home }
                            )
                            Screen.SignUp -> SignUpScreen(
                                viewModel        = authVM,
                                onSignUpComplete = { selectedScreen = Screen.Home },
                                onCancel         = { selectedScreen = Screen.Auth }
                            )
                            Screen.Home -> {
                                val goToActivityList by mapVM.goToActivityList.collectAsState()

                                LaunchedEffect(goToActivityList) {
                                    if (goToActivityList) {
                                        selectedScreen = Screen.ActivityList
                                        mapVM.resetNavigationFlag()
                                    }
                                }

                                val activities by actVM.activities.collectAsState()
                                LaunchedEffect(activities) {
                                    mapVM.updateMarkersFromActivities(activities)
                                }
                                mapVM.getUserLocation(this@MainActivity, fusedLocationClient)
                                MapGoogle(
                                    mapViewModel      = mapVM,
                                    activityViewModel = actVM
                                )
                            }
                            Screen.ActivityList -> ActivityListScreen(
                                viewModel           = actVM,
                                onAddActivityClick  = { selectedScreen = Screen.AddActivity },
                                onEditActivityClick = { activity ->
                                    selectedScreen = Screen.EditActivity
                                }
                            )
                            Screen.AddActivity -> AddActivityScreen(
                                viewModel = actVM,
                                onBack    = { selectedScreen = Screen.ActivityList }
                            )
                            Screen.EditActivity -> EditActivityScreen(
                                viewModel = actVM,
                                activity  = actVM.activities.collectAsState().value.first(),
                                onDone    = { selectedScreen = Screen.ActivityList }
                            )
                            Screen.Settings -> SettingsScreen(mapVM)
                        }
                    }
                }
            }
        }
    }
}

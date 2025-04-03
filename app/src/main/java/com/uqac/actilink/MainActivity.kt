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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uqac.actilink.ui.screens.ActivityListScreen
import com.uqac.actilink.ui.screens.AddActivityScreen
import com.uqac.actilink.ui.screens.AuthScreen
import com.uqac.actilink.ui.screens.BottomMenuBar
import com.uqac.actilink.ui.screens.MapGoogle
import com.uqac.actilink.ui.screens.SignUpScreen
import com.uqac.actilink.ui.theme.ActiLinkTheme
import com.uqac.actilink.viewmodel.ActivityViewModel
import com.uqac.actilink.viewmodel.AuthViewModel
import com.uqac.actilink.viewmodel.MapViewModel

/**
 * Enum pour gérer la navigation entre différents écrans.
 * On a ajouté SignUp pour un formulaire d'inscription complet.
 */
enum class Screen {
    Home,
    ActivityList,
    AddActivity,
    Settings,
    Auth,
    SignUp
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Pour la géolocalisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // ViewModels
        val mapViewModel: MapViewModel by viewModels()
        val authViewModel: AuthViewModel by viewModels()
        val activityViewModel: ActivityViewModel by viewModels()

        setContent {
            ActiLinkTheme {
                // État local : utilisateur Firebase courant
                var currentUser by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }
                // Permet de savoir si la permission GPS est accordée
                var permissionGranted by remember { mutableStateOf(false) }
                // Écran sélectionné. On démarre sur Screen.Auth pour forcer l'auth si user non connecté
                var selectedScreen by remember { mutableStateOf(Screen.Auth) }

                // Lanceur pour demander la permission de localisation
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    permissionGranted = isGranted
                }

                /**
                 * 1) On écoute les changements d’état de FirebaseAuth.
                 *    Dès que l’utilisateur se connecte/déconnecte, cette callback est déclenchée.
                 */
                LaunchedEffect(auth) {
                    auth.addAuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser


                        // Si l’utilisateur se déconnecte (devient null),
                        // on force l’écran Auth.
                        if (currentUser == null) {
                            selectedScreen = Screen.Auth
                        }
                    }
                }

                /**
                 * 2) Dès qu’on a un utilisateur valide (currentUser != null),
                 *    on gère la permission de localisation.
                 */
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        val permissionStatus = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = true
                        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            // Si on doit expliquer la permission
                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            // On demande la permission
                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }

                /**
                 * 3) Scaffold principal
                 *    - On affiche toujours la BottomBar,
                 *      mais on désactive les icônes si l’utilisateur n’est pas connecté.
                 */
                Scaffold(
                    bottomBar = {
                        BottomMenuBar(
                            selectedScreen = selectedScreen,
                            onScreenSelected = { newScreen ->
                                selectedScreen = newScreen
                            },
                            // Indique au BottomMenuBar si l'utilisateur est connecté
                            isAuthenticated = (currentUser != null)
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // 4) Navigation par when (selectedScreen)
                        when (selectedScreen) {

                            // Écran Auth
                            Screen.Auth -> {
                                AuthScreen(
                                    viewModel = authViewModel,
                                    // Si on veut aller sur l'écran d'inscription
                                    onSignUpClick = {
                                        selectedScreen = Screen.SignUp
                                    },
                                    // Au succès de la connexion, on navigue sur Home
                                    onLoginSuccess = {
                                        selectedScreen = Screen.Home
                                    }
                                )
                            }

                            // Écran d'inscription complète (SignUpScreen)
                            Screen.SignUp -> {
                                SignUpScreen(
                                    viewModel = authViewModel,
                                    onSignUpComplete = {
                                        // Quand l’inscription est réussie, l’utilisateur est logué => on va sur Home
                                        selectedScreen = Screen.Home
                                    },
                                    onCancel = {
                                        // Si l’utilisateur annule ou veut revenir en arrière, on retourne à l’écran Auth
                                        selectedScreen = Screen.Auth
                                    }
                                )
                            }

                            Screen.Home -> {
                                // Si l'utilisateur est connecté, on affiche la carte
                                if (currentUser != null) {
                                    // Récupère la liste des activités pour en faire des marqueurs
                                    val activities by activityViewModel.activities.collectAsState()
                                    LaunchedEffect(activities) {
                                        mapViewModel.updateMarkersFromActivities(activities)
                                    }
                                    mapViewModel.getUserLocation(this@MainActivity, fusedLocationClient)
                                    MapGoogle(mapViewModel = mapViewModel, activityViewModel = activityViewModel)
                                } else {
                                    // Sinon, on retourne sur Auth
                                    selectedScreen = Screen.Auth
                                }
                            }

                            Screen.ActivityList -> {
                                if (currentUser != null) {
                                    ActivityListScreen(
                                        viewModel = activityViewModel,
                                        onAddActivityClick = {
                                            selectedScreen = Screen.AddActivity
                                        }
                                    )
                                } else {
                                    selectedScreen = Screen.Auth
                                }
                            }

                            Screen.AddActivity -> {
                                // Écran de création d’activité
                                if (currentUser != null) {
                                    AddActivityScreen(
                                        viewModel = activityViewModel,
                                        onBack = {
                                            selectedScreen = Screen.ActivityList
                                        }
                                    )
                                } else {
                                    selectedScreen = Screen.Auth
                                }
                            }

                            Screen.Settings -> {
                                if (currentUser != null) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Settings Screen", style = MaterialTheme.typography.titleLarge)
                                    }
                                } else {
                                    selectedScreen = Screen.Auth
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

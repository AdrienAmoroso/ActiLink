package com.uqac.actilink.ui.screens

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import com.uqac.actilink.Screen

/**
 * @param selectedScreen L’écran actuel pour colorer l’icône correspondante
 * @param onScreenSelected Callback pour changer d’écran
 * @param isAuthenticated Indique si l’utilisateur est connecté ou non (pour désactiver certains boutons)
 **/
@Composable
fun BottomMenuBar(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    isAuthenticated: Boolean
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        // -- Home --
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.Home) },
            enabled = isAuthenticated // on active le bouton seulement si connecté
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = if (selectedScreen == Screen.Home && isAuthenticated) Color.Yellow else Color.White
            )
        }

        // -- ActivityList --
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.ActivityList) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.Event,
                contentDescription = "Liste d'activités",
                tint = if (selectedScreen == Screen.ActivityList && isAuthenticated) Color.Yellow else Color.White
            )
        }

        // -- AddActivity --
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.AddActivity) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = "Ajouter une activité",
                tint = if (selectedScreen == Screen.AddActivity && isAuthenticated) Color.Yellow else Color.White
            )
        }

        // -- Settings --
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.Settings) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = if (selectedScreen == Screen.Settings && isAuthenticated) Color.Yellow else Color.White
            )
        }

        // -- Auth (Connexion / Profil) --
        IconButton(
            onClick = { onScreenSelected(Screen.Auth) },
            enabled = true  // toujours actif pour autoriser la connexion/déconnexion
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Auth",
                tint = if (selectedScreen == Screen.Auth) Color.Yellow else Color.White
            )
        }
    }
}
// ui/screens/BottomMenuBar.kt
package com.uqac.actilink.ui.screens

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.uqac.actilink.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Map          // <-- icône Carte
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material.icons.filled.PendingActions


@Composable
fun BottomMenuBar(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    isAuthenticated: Boolean
) {
    BottomAppBar(containerColor = MaterialTheme.colorScheme.primary) {
        // Carte (remplace la maison)
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.Home) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.TravelExplore,
                contentDescription = "Carte",
                tint = if (selectedScreen == Screen.Home) Color.Yellow else Color.White
            )
        }

        // Liste d'activités
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.ActivityList) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.PendingActions ,
                contentDescription = "Liste d'activités",
                tint = if (selectedScreen == Screen.ActivityList) Color.Yellow else Color.White
            )
        }

        // Ajouter une activité
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.AddActivity) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = "Ajouter une activité",
                tint = if (selectedScreen == Screen.AddActivity) Color.Yellow else Color.White
            )
        }

        // Paramètres
        IconButton(
            onClick = { if (isAuthenticated) onScreenSelected(Screen.Settings) },
            enabled = isAuthenticated
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Paramètres",
                tint = if (selectedScreen == Screen.Settings) Color.Yellow else Color.White
            )
        }

        // Profil / Authentification
        IconButton(
            onClick = { onScreenSelected(Screen.Auth) },
            enabled = true
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profil",
                tint = if (selectedScreen == Screen.Auth) Color.Yellow else Color.White
            )
        }
    }
}

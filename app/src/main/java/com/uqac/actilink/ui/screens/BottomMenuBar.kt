package com.uqac.actilink

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomMenuBar(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        IconButton(onClick = { onScreenSelected(Screen.Home) }) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = if (selectedScreen == Screen.Home) Color.Yellow else Color.White
            )
        }
        IconButton(onClick = { onScreenSelected(Screen.Activity) }) {
            Icon(
                imageVector = Icons.Filled.Event,
                contentDescription = "Activités",
                tint = if (selectedScreen == Screen.Activity) Color.Yellow else Color.White
            )
        }
        IconButton(onClick = { onScreenSelected(Screen.Settings) }) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = if (selectedScreen == Screen.Settings) Color.Yellow else Color.White
            )
        }
        IconButton(onClick = { onScreenSelected(Screen.Auth) }) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Auth",
                tint = if (selectedScreen == Screen.Auth) Color.Yellow else Color.White
            )
        }
    }
}

// ui/screens/SettingsScreen.kt
package com.uqac.actilink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.MapViewModel

@Composable
fun SettingsScreen(mapViewModel: MapViewModel) {
    // <-- collectAsState() + getValue
    val allowedDistance by mapViewModel.allowedDistance.collectAsState()

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text  = "Distance de visibilité : ${allowedDistance.toInt()} km",
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value         = allowedDistance,
            onValueChange = { mapViewModel.setAllowedDistance(it) },
            valueRange    = 1f..160f,
            steps         = 159,
            modifier      = Modifier.padding(top = 16.dp)
        )
    }
}

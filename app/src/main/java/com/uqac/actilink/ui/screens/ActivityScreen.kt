package com.uqac.actilink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.ActivityViewModel

@Composable
fun ActivityScreen(viewModel: ActivityViewModel) {
    val activities by viewModel.activities.collectAsState()

    var title by remember { mutableStateOf(TextFieldValue()) }
    var date by remember { mutableStateOf(TextFieldValue()) }
    var location by remember { mutableStateOf(TextFieldValue()) }
    var latitude by remember { mutableStateOf(TextFieldValue()) }
    var longitude by remember { mutableStateOf(TextFieldValue()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ajouter une activité", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Lieu") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (title.text.isNotEmpty() && date.text.isNotEmpty() &&
                    location.text.isNotEmpty() && latitude.text.isNotEmpty() &&
                    longitude.text.isNotEmpty()
                ) {
                    val lat = latitude.text.toDoubleOrNull() ?: 0.0
                    val lng = longitude.text.toDoubleOrNull() ?: 0.0
                    viewModel.addActivity(title.text, date.text, location.text, "user_123", lat, lng)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ajouter")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text("Liste des activités", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(activities) { activity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = activity.title, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Lieu : ${activity.location}")
                        Text(text = "Date : ${activity.dateTime}")
                        Text(text = "Coordonnées : ${activity.latitude}, ${activity.longitude}")
                    }
                }
            }
        }
    }
}

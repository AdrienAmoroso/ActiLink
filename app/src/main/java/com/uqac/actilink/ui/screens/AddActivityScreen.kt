package com.uqac.actilink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.uqac.actilink.viewmodel.ActivityViewModel

@Composable
fun AddActivityScreen(viewModel: ActivityViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var date by remember { mutableStateOf(TextFieldValue()) }
    var location by remember { mutableStateOf(TextFieldValue()) }
    var latitude by remember { mutableStateOf(TextFieldValue()) }
    var longitude by remember { mutableStateOf(TextFieldValue()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

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

                    // On appelle la fonction de création dans le ViewModel
                    if (userId != null) {
                        viewModel.addActivity(
                            title = title.text,
                            date = date.text,
                            location = location.text,
                            userId = userId,  // A remplacer par l'ID actuel
                            latitude = lat,
                            longitude = lng
                        )
                    }

                    // Option : vider les champs ou revenir à la liste
                    // Ici on redirige vers la liste
                    onBack()
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Valider")
        }

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Annuler")
        }
    }
}

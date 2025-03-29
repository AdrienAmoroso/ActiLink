package com.uqac.actilink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.ActivityViewModel

@Composable
fun ActivityListScreen(
    viewModel: ActivityViewModel,
    onAddActivityClick: () -> Unit
) {
    // On récupère la liste filtrée depuis le ViewModel
    val filteredActivities by viewModel.filteredActivities.collectAsState()
    // On récupère aussi la valeur actuelle du filtre
    val currentFilter by viewModel.filterText.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Titre
        Text("Liste des activités", style = MaterialTheme.typography.titleLarge)

        // Bouton pour aller vers l'écran de création
        Button(
            onClick = { onAddActivityClick() },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Ajouter une activité")
        }

        // Champ de recherche pour filtrer
        OutlinedTextField(
            value = currentFilter,
            onValueChange = { newText ->
                viewModel.updateFilter(newText)
            },
            label = { Text("Filtrer les activités (titre, lieu ou type)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Affichage de la liste filtrée
        LazyColumn {
            items(filteredActivities) { activity ->
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
                        Text(text = "Type : ${activity.type}")
                        Text(text = "Coordonnées : ${activity.latitude}, ${activity.longitude}")
                    }
                }
            }
        }
        // Bouton pour peuplé la bdd
        /*Button(onClick = { viewModel.populateChicoutimi() }) {
            Text("Populer Chicoutimi")
        }*/

    }
}

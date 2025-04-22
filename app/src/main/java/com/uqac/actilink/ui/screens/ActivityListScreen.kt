package com.uqac.actilink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.ActivityViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ActivityListScreen(
    viewModel: ActivityViewModel,
    onAddActivityClick: () -> Unit
) {
    // On récupère la liste filtrée depuis le ViewModel
    val filteredActivities by viewModel.filteredActivities.collectAsState()
    // On récupère aussi la valeur actuelle du filtre
    val currentFilter by viewModel.filterText.collectAsState()

    val joinedActivities by viewModel.joinedActivities.collectAsState()

    LaunchedEffect(joinedActivities) {
        viewModel.loadActivities()
    }

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
                        Text(text = "creator : ${activity.creatorId}")
                        Text(text = "Participants : ${activity.participants.size}") // Vérifie bien la taille

                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        if (userId != null && !activity.participants.contains(userId)) {
                            Button(
                                onClick = { viewModel.joinActivity(activity.id) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Participer")
                            }
                        } else {
                            Text("Vous participez déjà à cette activité", color = MaterialTheme.colorScheme.primary)
                            Button(
                                onClick = { viewModel.leaveActivity(activity.id) },
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Quitter")
                            }
                        }

                        // Vérifie si l'utilisateur est le créateur de l'activité
                        if (userId == activity.creatorId) {
                            Button(
                                onClick = { viewModel.deleteActivity(activity.id) },
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Supprimer")
                            }
                        }
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

// ui/screens/ActivityListScreen.kt
package com.uqac.actilink.ui.screens

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.viewmodel.ActivityViewModel

@Composable
fun ActivityListScreen(
    viewModel: ActivityViewModel,
    onAddActivityClick: () -> Unit,
    onEditActivityClick: (ActivityModel) -> Unit
) {
    val filteredActivities by viewModel.filteredActivities.collectAsState()
    val currentFilter       by viewModel.filterText.collectAsState()
    val joinedActivities    by viewModel.joinedActivities.collectAsState()

    // recharge la liste à chaque changement
    LaunchedEffect(joinedActivities) { viewModel.loadActivities() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Liste des activités", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick  = onAddActivityClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter une activité")
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value         = currentFilter,
            onValueChange = { viewModel.updateFilter(it) },
            label         = { Text("Filtrer (titre, lieu, type)") },
            modifier      = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(filteredActivities) { activity ->
                // formattage "10h30-15h00"
                val timeText = activity.startTime.replace(":", "h") +
                        "-" +
                        activity.endTime.replace(":", "h")

                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(activity.title, style = MaterialTheme.typography.titleLarge)
                        Text("Lieu : ${activity.location}")
                        Text("Date : ${activity.dateTime}")
                        Text("Heure : $timeText")
                        Text("Participants : ${activity.participants.size}")

                        Spacer(modifier = Modifier.height(8.dp))

                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        Row(
                            modifier            = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Modifier (couleur personnalisée)
                            if (userId == activity.creatorId) {
                                Button(
                                    onClick = { onEditActivityClick(activity) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor   = MaterialTheme.colorScheme.onSecondary
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Modifier")
                                }
                            }
                            // Participer
                            if (userId != null && !activity.participants.contains(userId)) {
                                Button(
                                    onClick  = { viewModel.joinActivity(activity.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Participer")
                                }
                            }
                            // Quitter
                            else if (userId != null) {
                                Button(
                                    onClick = { viewModel.leaveActivity(activity.id) },
                                    colors  = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Quitter")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

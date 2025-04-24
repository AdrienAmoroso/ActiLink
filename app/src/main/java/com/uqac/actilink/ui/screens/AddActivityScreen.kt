// ui/screens/AddActivityScreen.kt
package com.uqac.actilink.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.uqac.actilink.ui.screens.MapScreen
import com.uqac.actilink.viewmodel.ActivityViewModel
import java.util.*

@Composable
fun AddActivityScreen(
    viewModel: ActivityViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(TextFieldValue()) }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(TextFieldValue()) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var isMapVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "user_123"

    // Android DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year, month, day ->
                date = "%02d/%02d/%04d".format(day, month + 1, year)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Android TimePickerDialog for start time
    if (showStartTimePicker) {
        TimePickerDialog(
            context,
            { _: TimePicker, hour, minute ->
                startTime = "%02d:%02d".format(hour, minute)
                showStartTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Android TimePickerDialog for end time
    if (showEndTimePicker) {
        TimePickerDialog(
            context,
            { _: TimePicker, hour, minute ->
                endTime = "%02d:%02d".format(hour, minute)
                showEndTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Si on veut choisir la position sur la carte
    if (isMapVisible) {
        MapScreen { latLng ->
            selectedLatLng = latLng
            isMapVisible = false
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ajouter une activité", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { showDatePicker = true }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Sélectionner la date")
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = startTime,
                onValueChange = { },
                label = { Text("Heure de début") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { showStartTimePicker = true }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Sélectionner l'heure de début")
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = endTime,
                onValueChange = { },
                label = { Text("Heure de fin") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { showEndTimePicker = true }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Sélectionner l'heure de fin")
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { isMapVisible = true }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Sélectionner sur la carte")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.text.isNotBlank()
                        && date.isNotBlank()
                        && startTime.isNotBlank()
                        && endTime.isNotBlank()
                        && location.text.isNotBlank()
                        && selectedLatLng != null
                    ) {
                        viewModel.addActivity(
                            title = title.text,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            location = location.text,
                            userId = userId,
                            latitude = selectedLatLng!!.latitude,
                            longitude = selectedLatLng!!.longitude
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider")
            }
        }
    }
}

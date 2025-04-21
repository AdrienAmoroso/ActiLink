package com.uqac.actilink.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.maps.model.LatLng
import com.uqac.actilink.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(viewModel: ActivityViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(TextFieldValue()) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var isMapVisible by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    var userId = FirebaseAuth.getInstance().currentUser?.uid
    val datePickerState = rememberDatePickerState()

    if (userId == null) {
        // Handle the case where the user is not logged in
        userId = "user_123"
    }

    if (isMapVisible) {
        MapScreen { latLng ->
            selectedLatLng = latLng
            isMapVisible = false
        }
    } else {
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
                onValueChange = {
                    // You can add logic to parse and validate manual input here, if needed
                    // For example, check if the entered format matches "dd/MM/yyyy"
                    date = it
                },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = true
            )

            Button(onClick = { showDatePickerDialog = true }) {
                Text("Sélectionner une date")
            }

            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            showDatePickerDialog = false
                            // validate the date here.
                            val selectedDateMillis = datePickerState.selectedDateMillis
                            if (selectedDateMillis != null && selectedDateMillis >= System.currentTimeMillis()) {
                                date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
                            } else {
                                //handle invalid date
                            }
                        }) {
                            Text("Valider")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePickerDialog = false }) {
                            Text("Annuler")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { isMapVisible = true },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Sélectionner sur la carte")
            }

            Button(
                onClick = {
                    if (title.text.isNotEmpty() && date.isNotEmpty() &&
                        location.text.isNotEmpty() && selectedLatLng != null
                    ) {
                        val lat = selectedLatLng!!.latitude
                        val lng = selectedLatLng!!.longitude
                        viewModel.addActivity(
                            title = title.text,
                            date = date,
                            location = location.text,
                            userId = userId,
                            latitude = lat,
                            longitude = lng
                        )
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
}
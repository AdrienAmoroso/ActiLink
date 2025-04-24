
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
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.viewmodel.ActivityViewModel
import java.util.*

@Composable
fun EditActivityScreen(
    viewModel: ActivityViewModel,
    activity: ActivityModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(TextFieldValue(activity.title)) }
    var date by remember { mutableStateOf(activity.dateTime) }
    var startTime by remember { mutableStateOf(activity.startTime) }
    var endTime by remember { mutableStateOf(activity.endTime) }
    var location by remember { mutableStateOf(TextFieldValue(activity.location)) }
    var selectedLatLng by remember { mutableStateOf(LatLng(activity.latitude, activity.longitude)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply {
        // init calendar to activity date/time if needed
    }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year, month, day ->
                date = "%02d/%02d/%04d".format(day, month+1, year)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    if (showStartPicker) {
        TimePickerDialog(
            context,
            { _: TimePicker, h, m ->
                startTime = "%02d:%02d".format(h, m)
                showStartPicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
    if (showEndPicker) {
        TimePickerDialog(
            context,
            { _: TimePicker, h, m ->
                endTime = "%02d:%02d".format(h, m)
                showEndPicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    if (showMap) {
        MapScreen { latLng ->
            selectedLatLng = latLng
            showMap = false
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Modifier l'activité", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(title, { title = it }, label = { Text("Titre") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(TextFieldValue(date), {}, label = { Text("Date") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Button(onClick = { showDatePicker = true }) { Text("Date") }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(TextFieldValue(startTime), {}, label = { Text("Début") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Button(onClick = { showStartPicker = true }) { Text("Début") }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(TextFieldValue(endTime), {}, label = { Text("Fin") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Button(onClick = { showEndPicker = true }) { Text("Fin") }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(location, { location = it }, label = { Text("Lieu") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { showMap = true }) { Text("Carte") }

            Spacer(Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        // Update
                        viewModel.updateActivity(
                            activity.copy(
                                title = title.text,
                                dateTime = date,
                                startTime = startTime,
                                endTime = endTime,
                                location = location.text,
                                latitude = selectedLatLng.latitude,
                                longitude = selectedLatLng.longitude
                            )
                        )
                        onDone()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enregistrer")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.deleteActivity(activity.id)
                        onDone()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

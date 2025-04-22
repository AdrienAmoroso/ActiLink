package com.uqac.actilink.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.AuthViewModel

/**
 * [AuthScreen] gère l’écran de connexion
 *
 * @param onSignUpClick Callback appelé lorsque l’utilisateur veut aller vers
 *                      un écran d’inscription (SignUpScreen)
 * @param onLoginSuccess Callback appelé lorsque la connexion est réussie
 *
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit  // On gardera si tu veux l’utiliser depuis un bouton "Aller à la carte"
) {
    // Champs, erreurs, etc.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val joinedActivities by viewModel.joinedActivities.collectAsState()
    val profile by viewModel.userProfile.collectAsState()


    val authMessage by viewModel.authMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(joinedActivities) {
        viewModel.loadJoinedActivities()
    }


    if (!isAuthenticated) {
        // -----------------------
        // UTILISATEUR NON CONNECTÉ => FORMULAIRE DE CONNEXION
        // -----------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { input ->
                    email = input
                    emailError = if (input.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                        "Email invalide"
                    } else null
                },
                label = { Text("Email") },
                isError = (emailError != null),
                modifier = Modifier.fillMaxWidth()
            )
            emailError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { input ->
                    password = input
                    passwordError = if (input.isEmpty()) "Mot de passe requis" else null
                },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                isError = (passwordError != null),
                modifier = Modifier.fillMaxWidth()
            )
            passwordError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton "Se connecter"
            Button(
                onClick = { viewModel.login(email, password) },
                enabled = (emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se connecter")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton "S'inscrire"
            Button(
                onClick = { onSignUpClick() },
                enabled = (emailError == null && passwordError == null),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("S'inscrire")
            }
        }
    } else {
        // -----------------------
        // UTILISATEUR CONNECTÉ => PAGE "PROFIL"
        // -----------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            profile?.let {
                Text("Bienvenue, ${it.name} 👋", style = MaterialTheme.typography.headlineSmall)
                Text("Âge : ${it.age}", style = MaterialTheme.typography.bodyMedium)
                Text("Bio : ${it.bio}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text("Activités rejointes :", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (joinedActivities.isEmpty()) {
                Text("Aucune activité rejointe pour l’instant.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Permet à la liste de prendre tout l'espace restant
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(joinedActivities) { activity ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(activity.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Lieu : ${activity.location}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Date : ${activity.dateTime}", style = MaterialTheme.typography.bodySmall)
                                }

                                Button(
                                    onClick = { viewModel.leaveActivity(activity.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(12.dp)
                                ) {
                                    Text("Quitter", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se déconnecter", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onLoginSuccess,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Aller à l'accueil")
            }
        }
    }
}


package com.uqac.actilink.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // Champs du formulaire
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    // Erreurs de validation
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Observe l'état d'authentification
    val authMessage by viewModel.authMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // Affiche le message d’erreur ou de succès de Firebase (renvoyé par AuthViewModel)

    // Si déjà connecté => l’inscription est terminée avec succès
    if (isAuthenticated) {
        LaunchedEffect(Unit) {
            onSignUpComplete()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Créer un compte", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Champ Nom
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Champ Email
            OutlinedTextField(
                value = email,
                onValueChange = { input ->
                    email = input
                    // Validation
                    emailError = if (input.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                        "Email invalide"
                    } else null
                },
                label = { Text("Email") },
                isError = (emailError != null),
                modifier = Modifier.fillMaxWidth()
            )
            // Affichage d'erreur pour l'email
            emailError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Champ Mot de passe
            OutlinedTextField(
                value = password,
                onValueChange = { input ->
                    password = input
                    // Vérification minimale (longueur > 5 par exemple)
                    passwordError = if (input.length < 6) {
                        "Mot de passe trop court (6 caractères minimum)"
                    } else null
                },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                isError = (passwordError != null),
                modifier = Modifier.fillMaxWidth()
            )
            // Affichage d'erreur pour le mot de passe
            passwordError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Champ Confirmation du mot de passe
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { input ->
                    confirmPassword = input
                    // Vérification correspondance mot de passe
                    confirmPasswordError = if (input != password) {
                        "Les mots de passe ne correspondent pas"
                    } else null
                },
                label = { Text("Confirmer le mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                isError = (confirmPasswordError != null),
                modifier = Modifier.fillMaxWidth()
            )
            // Affichage d'erreur pour la confirmation
            confirmPasswordError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Champ Âge
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Âge") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Champ Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Validation
            Button(
                onClick = {
                    // Vérifier que tous les champs sont valides avant d'appeler register
                    val noErrors = (emailError == null && passwordError == null && confirmPasswordError == null)
                            && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()

                    if (noErrors) {
                        viewModel.registerWithProfile(
                            email = email,
                            password = password,
                            name = name,
                            age = age.toIntOrNull() ?: 0,
                            bio = bio
                        ) { profileCreated ->
                            if (!profileCreated) {
                                Toast.makeText(context, "Erreur lors de la création du profil ❌", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Veuillez corriger les champs invalides.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider l'inscription")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton Annuler => retour à l'écran Auth
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler")
            }
        }
    }
}

package com.uqac.actilink.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uqac.actilink.viewmodel.AuthViewModel

@Composable
fun AuthScreen( viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authMessage by viewModel.authMessage.observeAsState(null)
    val userId by viewModel.userId.observeAsState(null)
    val isAuthenticated by viewModel.isAuthenticated.observeAsState(false)

    // Affichage du message d'authentification (réussi ou échoué)
    authMessage?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
    }

    // Affichage du contenu en fonction de l'état d'authentification
    if (isAuthenticated) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text("Utilisateur connecté avec ID: $userId")
            Button(onClick = { viewModel.logout() }) {
                Text("Se déconnecter")
            }
        }
    } else {
        // Sinon, afficher le formulaire pour se connecter ou s'inscrire
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { viewModel.login(email, password) }) {
                Text("Se connecter")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { viewModel.register(email, password) }) {
                Text("S'inscrire")
            }
        }
    }
}

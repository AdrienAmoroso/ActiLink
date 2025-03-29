package com.uqac.actilink.services

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.uqac.actilink.models.ActivityModel


class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Fonction pour s'inscrire
    fun registerUser(email: String, password: String, onResult: (Boolean, String?, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    onResult(true, "Inscription réussie ✅", userId)
                } else {
                    onResult(false, task.exception?.message, null)
                }
            }
    }

    // Fonction pour se connecter
    fun loginUser(email: String, password: String, onResult: (Boolean, String?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    Log.d("FirebaseAuth", "Connexion réussie : ${user?.email}")
                    onResult(true, "Connexion réussie ✅", userId)
                } else {
                    Log.e("FirebaseAuth", "Échec de connexion", task.exception)
                    onResult(false, task.exception?.message ?: "Erreur inconnue ❌", null)
                }
            }
    }

    // Vérifier si un utilisateur est connecté
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Récupérer l'ID de l'utilisateur connecté
    fun getUserId(): String? = auth.currentUser?.uid

    // Déconnexion
    fun logoutUser() {
        auth.signOut()
    }

    // Ajouter une activité
    suspend fun addActivity(activity: ActivityModel): Boolean {
        return try {
            db.collection("activities").document(activity.id).set(activity).await()
            Log.d("Firestore", "Activité ajoutée avec ID : ${activity.id}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur lors de l'ajout", e)
            false
        }
    }

    // Récupérer toutes les activités
    suspend fun getActivities(): List<ActivityModel> {
        return try {
            val result = db.collection("activities").get().await()
            result.documents.mapNotNull { it.toObject(ActivityModel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
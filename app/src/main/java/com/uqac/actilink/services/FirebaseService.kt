package com.uqac.actilink.services

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    suspend fun deleteActivity(activityId: String): Result<Unit> {
        return try {
            db.collection("activities").document(activityId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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

    suspend fun joinActivity(activityId: String): Result<Unit> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure(Exception("Utilisateur non authentifié"))
        val activityRef = db.collection("activities").document(activityId)

        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(activityRef)
                val currentParticipants = snapshot.get("participants") as? List<String> ?: emptyList()

                if (!currentParticipants.contains(userId)) {
                    val updatedParticipants = currentParticipants + userId
                    transaction.update(activityRef, "participants", updatedParticipants)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveActivity(activityId: String): Result<Unit> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure(Exception("Utilisateur non authentifié"))
        val activityRef = db.collection("activities").document(activityId)
        return try {

            // Supprime l'utilisateur de la liste des participants
            activityRef.update("participants", FieldValue.arrayRemove(userId)).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fonction pour peuplé la bdd
    /*
    suspend fun populateChicoutimiActivities(): Boolean {
        return try {
            // Exemple de liste d’activités autour de Chicoutimi
            val fakeActivities = listOf(
                ActivityModel(
                    id = "chico-001",
                    title = "Randonnée au Parc de la Rivière-du-Moulin",
                    type = "Randonnée",
                    dateTime = "2025-05-10",
                    location = "Chicoutimi",
                    creatorId = "admin",
                    participants = emptyList(),
                    latitude = 48.409995,  // Coordonnées approximatives
                    longitude = -71.045893
                ),
                ActivityModel(
                    id = "chico-002",
                    title = "Course matinale sur le pont Dubuc",
                    type = "Course",
                    dateTime = "2025-05-11",
                    location = "Chicoutimi",
                    creatorId = "admin",
                    participants = emptyList(),
                    latitude = 48.423930,
                    longitude = -71.065071
                ),
                ActivityModel(
                    id = "chico-003",
                    title = "Session de hockey au Centre Georges-Vézina",
                    type = "Hockey",
                    dateTime = "2025-06-01",
                    location = "Saguenay (Chicoutimi)",
                    creatorId = "admin",
                    participants = emptyList(),
                    latitude = 48.422275,
                    longitude = -71.055049
                ),
                ActivityModel(
                    id = "chico-004",
                    title = "Atelier yoga au bord de la rivière Saguenay",
                    type = "Yoga",
                    dateTime = "2025-06-05",
                    location = "Chicoutimi",
                    creatorId = "admin",
                    participants = emptyList(),
                    latitude = 48.416580,
                    longitude = -71.058880
                ),
                ActivityModel(
                    id = "chico-005",
                    title = "Marche culturelle dans le centre-ville",
                    type = "Marche",
                    dateTime = "2025-07-10",
                    location = "Centre-ville Chicoutimi",
                    creatorId = "admin",
                    participants = emptyList(),
                    latitude = 48.427300,
                    longitude = -71.068700
                )
            )

            // Pour chaque activité, on l'ajoute dans Firestore
            fakeActivities.forEach { activity ->
                db.collection("activities")
                    .document(activity.id)
                    .set(activity)
                    .await()
                Log.d("Firestore", "Activité '${activity.id}' ajoutée !")
            }

            // Si tout se passe bien, on renvoie true
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Erreur lors du peuplement des activités", e)
            false
        }
    }
    */


}
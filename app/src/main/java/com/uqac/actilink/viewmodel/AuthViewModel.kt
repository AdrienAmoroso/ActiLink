// viewmodel/AuthViewModel.kt
package com.uqac.actilink.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.models.UserProfile
import com.uqac.actilink.repository.ActivityRepository
import com.uqac.actilink.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val activityRepository: ActivityRepository = ActivityRepository()
) : ViewModel() {

    private val repository = FirebaseService()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    val joinedActivities: StateFlow<List<ActivityModel>> = activityRepository.joinedActivities

    init {
        // Au lancement du ViewModel, on vérifie si l'utilisateur est déjà connecté
        checkUserStatus()
    }

    /** Vérifie si un utilisateur est déjà connecté et charge ses données */
    fun checkUserStatus() {
        val uid = repository.getUserId()
        _userId.value = uid
        _isAuthenticated.value = uid != null
        if (uid != null) {
            loadJoinedActivities()
            loadUserProfile(uid)
        }
    }

    /** Charge le profil depuis Firestore */
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            repository.getUserProfile(userId)?.let {
                _userProfile.value = it
            }
        }
    }

    /** Charge les activités que l'utilisateur a rejointes */
    fun loadJoinedActivities() {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            activityRepository.loadJoinedActivities(uid)
        }
    }

    /** Effectue la connexion Firebase */
    fun login(email: String, password: String) {
        repository.loginUser(email, password) { success, message, uid ->
            _authMessage.value = message
            _isAuthenticated.value = success
            _userId.value = uid
            if (success && uid != null) {
                loadJoinedActivities()
                loadUserProfile(uid)
            }
        }
    }

    /** Inscription + création de profil */
    fun registerWithProfile(
        email: String,
        password: String,
        name: String,
        age: Int,
        bio: String,
        onProfileCreated: (Boolean) -> Unit
    ) {
        repository.registerUser(email, password) { success, message, uid ->
            _authMessage.value = message
            _isAuthenticated.value = success
            _userId.value = uid
            if (success && uid != null) {
                viewModelScope.launch {
                    val created = repository.createUserProfile(uid, name, age, bio)
                    onProfileCreated(created)
                    loadUserProfile(uid)
                }
            } else {
                onProfileCreated(false)
            }
        }
    }

    /** Déconnexion Firebase */
    fun logout() {
        repository.logoutUser()
        _isAuthenticated.value = false
        _userId.value = null
    }

    /** Quitter une activité */
    fun leaveActivity(activityId: String) {
        val uid = repository.getUserId() ?: return
        viewModelScope.launch {
            activityRepository.leaveActivity(activityId, uid)
            loadJoinedActivities()
        }
    }
}

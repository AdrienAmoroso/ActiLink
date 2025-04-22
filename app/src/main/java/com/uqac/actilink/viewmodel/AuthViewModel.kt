package com.uqac.actilink.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uqac.actilink.models.ActivityModel
import com.uqac.actilink.models.UserProfile
import com.uqac.actilink.repository.ActivityRepository
import com.uqac.actilink.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val activityRepository: ActivityRepository = ActivityRepository()) : ViewModel() {

    private val repository = FirebaseService()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    val joinedActivities: StateFlow<List<ActivityModel>> = activityRepository.joinedActivities

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfile(userId)
            _userProfile.value = profile
        }
    }

    fun loadJoinedActivities() {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            activityRepository.loadJoinedActivities(userId)
        }
    }

    fun login(email: String, password: String) {
        repository.loginUser(email, password) { success, message, userId ->
            _authMessage.value = message
            _isAuthenticated.value = success
            _userId.value = userId

            if (success && userId != null) {
                loadJoinedActivities()
                loadUserProfile(userId)
            }
        }
    }

    fun registerWithProfile(
        email: String,
        password: String,
        name: String,
        age: Int,
        bio: String,
        onProfileCreated: (Boolean) -> Unit
    ) {
        repository.registerUser(email, password) { success, message, userId ->
            _authMessage.value = message
            _isAuthenticated.value = success
            _userId.value = userId

            if (success && userId != null) {
                // Crée le profil utilisateur
                kotlinx.coroutines.GlobalScope.launch {
                    val created = repository.createUserProfile(userId, name, age, bio)
                    onProfileCreated(created)
                    loadUserProfile(userId)
                }
            } else {
                onProfileCreated(false)
            }
        }
    }

    fun leaveActivity(activityId: String) {
        val userId = repository.getUserId() ?: return
        viewModelScope.launch {
            activityRepository.leaveActivity(activityId, userId)
            loadJoinedActivities()
        }
    }

    fun logout() {
        repository.logoutUser()
        _isAuthenticated.value = false
        _userId.value = null
    }

    fun checkUserStatus() {
        val userId = repository.getUserId()
        _userId.value = userId
        _isAuthenticated.value = userId != null
    }
}

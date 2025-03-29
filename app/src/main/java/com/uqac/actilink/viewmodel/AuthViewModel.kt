package com.uqac.actilink.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqac.actilink.services.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val repository = FirebaseService()


    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun register(email: String, password: String) {
        repository.registerUser(email, password) { success, message, userId ->
            _authMessage.value = message
            _isAuthenticated.value = success
            _userId.value = userId
        }
    }

    fun login(email: String, password: String) {
        repository.loginUser(email, password) { success, message, userId ->
            _authMessage.value = message
            _isAuthenticated.value = success
            _userId.value = userId
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

package com.uqac.actilink.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uqac.actilink.services.FirebaseService

class AuthViewModel : ViewModel() {
    private val repository = FirebaseService()

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    private val _authMessage = MutableLiveData<String?>()
    val authMessage: LiveData<String?> get() = _authMessage

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    fun register(email: String, password: String) {
        repository.registerUser(email, password) { success, message, userId ->
            _authMessage.postValue(message)
            _isAuthenticated.postValue(success)
            _userId.postValue(userId)
        }
    }

    fun login(email: String, password: String) {
        repository.loginUser(email, password) { success, message, userId ->
            _authMessage.postValue(message)
            _isAuthenticated.postValue(success)
            _userId.postValue(userId)
        }
    }

    fun logout() {
        repository.logoutUser()
        _isAuthenticated.postValue(false)
        _userId.postValue(null)
    }

    fun checkUserStatus() {
        val userId = repository.getUserId()
        _userId.postValue(userId)
        _isAuthenticated.postValue(userId != null)
    }
}
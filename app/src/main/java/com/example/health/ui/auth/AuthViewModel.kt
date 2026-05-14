package com.example.health.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(repository.currentUser != null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, password)
            _isLoading.value = false
            if (result.isSuccess) {
                _isLoggedIn.value = true
                onSuccess()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }

    fun register(name: String, email: String, password: String, mobile: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.register(name, email, password, mobile)
            _isLoading.value = false
            if (result.isSuccess) {
                _isLoggedIn.value = true
                onSuccess()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }

    fun logout() {
        repository.logout()
        _isLoggedIn.value = false
    }

    fun clearError() {
        _error.value = null
    }
}

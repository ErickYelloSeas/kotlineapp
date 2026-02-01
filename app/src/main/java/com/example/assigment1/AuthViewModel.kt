package com.example.assigment1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.login(mapOf("email" to email, "password" to password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data?.user
                    if (user != null) {
                        _currentUser.value = user
                        _authState.value = AuthState.Success(user)
                    } else {
                        _authState.value = AuthState.Error("User data missing")
                    }
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.logout()
                _currentUser.value = null
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                // Handle logout error
            }
        }
    }
}
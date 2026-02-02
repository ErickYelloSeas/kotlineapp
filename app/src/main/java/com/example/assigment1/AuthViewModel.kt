package com.example.assigment1

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getUser())
    val currentUser: StateFlow<User?> = _currentUser

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent: SharedFlow<String> = _navigationEvent

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter both email and password")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.login(mapOf("email" to email, "password" to password))
                Log.d("AuthViewModel", "Login response: ${response.code()}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("AuthViewModel", "Body success: ${body?.success}")
                    
                    // The server seems to return the User directly in 'data'
                    val user = body?.data
                    if (body?.success == true && user != null) {
                        Log.d("AuthViewModel", "Saving user session and navigating")
                        sessionManager.saveUser(user)
                        _currentUser.value = user
                        _authState.value = AuthState.Success(user)
                        _navigationEvent.emit("dashboard")
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Invalid credentials")
                    }
                } else {
                    val errorMsg = parseError(response.errorBody())
                    _authState.value = AuthState.Error(errorMsg ?: "Login failed")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error", e)
                _authState.value = AuthState.Error("Connection error: ${e.message}")
            }
        }
    }

    fun register(firstName: String, lastName: String, username: String, email: String, password: String) {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    roleId = 3 
                )
                val response = RetrofitClient.apiService.register(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        _authState.value = AuthState.Error("Account created! You can now login.")
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Registration failed")
                    }
                } else {
                    val errorMsg = parseError(response.errorBody())
                    _authState.value = AuthState.Error(errorMsg ?: "Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Connection error: ${e.message}")
            }
        }
    }

    private fun parseError(errorBody: ResponseBody?): String? {
        return try {
            val jsonString = errorBody?.string()
            val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
            jsonObject.get("message")?.asString
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        viewModelScope.launch {
            try { RetrofitClient.apiService.logout() } catch (e: Exception) {}
            sessionManager.clearSession()
            _currentUser.value = null
            _authState.value = AuthState.Idle
            _navigationEvent.emit("login")
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

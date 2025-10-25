package com.example.vivepasoapaso.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.User
import com.example.vivepasoapaso.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Estados para el perfil
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isUpdatingGoals = MutableStateFlow(false)
    val isUpdatingGoals: StateFlow<Boolean> = _isUpdatingGoals.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun signUp(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signUp(email, password, name)
            _authState.value = when {
                result.isSuccess -> {
                    _currentUser.value = result.getOrNull()
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            _authState.value = when {
                result.isSuccess -> {
                    _currentUser.value = result.getOrNull()
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }

    fun updateUserProfile(name: String, language: String) {
        viewModelScope.launch {
            val currentUser = _currentUser.value ?: return@launch
            val result = authRepository.updateUserProfile(currentUser.id, name, language)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _snackbarMessage.value = "Perfil actualizado correctamente"
            } else {
                _snackbarMessage.value = "Error al actualizar perfil: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    //Función para actualizar metas diarias
    fun updateDailyGoals(dailyGoals: com.example.vivepasoapaso.data.model.DailyGoals) {
        _isUpdatingGoals.value = true
        viewModelScope.launch {
            val currentUser = _currentUser.value ?: run {
                _snackbarMessage.value = "Error: Usuario no autenticado"
                _isUpdatingGoals.value = false
                return@launch
            }

            val result = authRepository.updateDailyGoals(currentUser.id, dailyGoals)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _snackbarMessage.value = "¡Metas actualizadas correctamente!"
            } else {
                _snackbarMessage.value = "Error al actualizar metas: ${result.exceptionOrNull()?.message}"
            }
            _isUpdatingGoals.value = false
        }
    }

    //Función para limpiar mensajes
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = authRepository.getCurrentUser()
            } catch (e: Exception) {
                //Ignorar errores al obtener usuario actual
            }
        }
    }

    fun clearAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
package com.example.vivepasoapaso.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.User
import com.example.vivepasoapaso.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _navigateToDashboard = MutableStateFlow(false)
    val navigateToDashboard: StateFlow<Boolean> = _navigateToDashboard.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

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
                    _navigateToLogin.value = true // Navegar a login después del registro
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
                    _navigateToDashboard.value = true
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(account)
            _authState.value = when {
                result.isSuccess -> {
                    _currentUser.value = result.getOrNull()
                    _navigateToDashboard.value = true
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error con Google Sign In")
            }
        }
    }

    fun signInWithFacebook(token: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithFacebook(token)
            _authState.value = when {
                result.isSuccess -> {
                    _currentUser.value = result.getOrNull()
                    _navigateToDashboard.value = true
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error con Facebook Sign In")
            }
        }
    }

    fun signInWithApple(idToken: String, nonce: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.signInWithApple(idToken, nonce)
            _authState.value = when {
                result.isSuccess -> {
                    _currentUser.value = result.getOrNull()
                    _navigateToDashboard.value = true
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error con Apple Sign In")
            }
        }
    }

    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.resetPassword(email)
            _authState.value = when {
                result.isSuccess -> {
                    _snackbarMessage.value = "Se ha enviado un enlace de recuperación a tu email"
                    AuthState.Success
                }
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Error al enviar email de recuperación")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _authState.value = AuthState.Idle
            _navigateToDashboard.value = false
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

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun resetNavigation() {
        _navigateToDashboard.value = false
        _navigateToLogin.value = false
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = authRepository.getCurrentUser()
            } catch (e: Exception) {
                // Ignorar errores al obtener usuario actual
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
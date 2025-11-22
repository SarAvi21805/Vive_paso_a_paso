package com.example.vivepasoapaso.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.User
import com.example.vivepasoapaso.data.repository.AuthRepository
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

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = "Error verificando usuario: ${e.message}"
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signIn(email, password)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    _errorMessage.value = "Error al iniciar sesión"
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = "Error al iniciar sesión: ${e.message}"
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signUp(email, password, name)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    _errorMessage.value = "Error al crear cuenta"
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = "Error al crear cuenta: ${e.message}"
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Para Google, necesitamos crear un GoogleSignInAccount temporal
                // o modificar AuthRepository para aceptar idToken directamente
                _errorMessage.value = "Google Sign-In necesita configuración adicional en AuthRepository"
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = "Error con Google Sign-In: ${e.message}"
            }
        }
    }

    fun signInWithFacebook(token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signInWithFacebook(token)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    _errorMessage.value = "Error con Facebook Sign-In"
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = "Error con Facebook Sign-In: ${e.message}"
            }
        }
    }

    fun signInWithApple(idToken: String, nonce: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signInWithApple(idToken, nonce)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Unauthenticated
                    _errorMessage.value = "Error con Apple Sign-In"
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = "Error con Apple Sign-In: ${e.message}"
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _errorMessage.value = "Error al cerrar sesión: ${e.message}"
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.resetPassword(email)
                if (result.isSuccess) {
                    _errorMessage.value = "Correo de restablecimiento enviado"
                } else {
                    _errorMessage.value = "Error al enviar correo de restablecimiento"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al restablecer contraseña: ${e.message}"
            }
        }
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}
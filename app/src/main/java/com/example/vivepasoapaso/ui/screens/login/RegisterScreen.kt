// en ui/screens/login/RegisterScreen.kt

package com.example.vivepasoapaso.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vivepasoapaso.presentation.auth.AuthState
import com.example.vivepasoapaso.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Observa el estado del registro
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
            viewModel.clearAuthState() // Limpia el estado para futuros intentos
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear Cuenta") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Únete a la comunidad!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Muestra errores si existen
            if (authState is AuthState.Error) {
                Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.signUp(email, password, name) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6
                ) {
                    Text("Crear Cuenta")
                }
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes una cuenta? Inicia Sesión")
            }
        }
    }
}
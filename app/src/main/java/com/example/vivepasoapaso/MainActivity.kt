package com.example.vivepasoapaso

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.vivepasoapaso.presentation.navigation.AppNavigation
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.example.vivepasoapaso.util.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint // Hilt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Preparamos el lanzador para solicitar el permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido. La notificación ya está programada.
        } else {
            // Permiso denegado. La app funcionará pero no mostrará notificaciones.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VivePasoAPasoTheme {
                // Usamos LaunchedEffect para pedir el permiso de forma segura al iniciar la UI
                LaunchedEffect(Unit) {
                    askNotificationPermission()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Usamos tu componente de navegación
                }
            }
        }
    }

    // Creamos la función que verifica la versión de Android y solicita el permiso
    private fun askNotificationPermission() {
        // Solo es necesario pedir permiso en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
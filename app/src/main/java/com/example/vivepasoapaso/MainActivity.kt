package com.example.vivepasoapaso

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.vivepasoapaso.presentation.auth.AuthViewModel
import com.example.vivepasoapaso.ui.navigation.AppNavigation
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    // Contract para Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    lifecycleScope.launch {
                        authViewModel.signInWithGoogle(idToken)
                    }
                }
            } catch (e: ApiException) {
                authViewModel.setError("Error en Google Sign-In: ${e.message}")
            }
        }
    }

    // Contract para Facebook Sign-In
    private val facebookSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Facebook maneja su propio callback a través del SDK
        // Este launcher se usa principalmente para iniciar la actividad de Facebook
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            VivePasoAPasoTheme {
                val authState by authViewModel.authState.collectAsState()

                AppNavigation(
                    authState = authState,
                    onGoogleSignIn = { launchGoogleSignIn() },
                    onFacebookSignIn = { launchFacebookSignIn() },
                    onAppleSignIn = { launchAppleSignIn() }
                )
            }
        }
    }

    private fun launchGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun launchFacebookSignIn() {
        // Facebook Login se maneja a través de su SDK en la UI de Compose
        // Este método puede usarse para preparar cualquier configuración necesaria
    }

    private fun launchAppleSignIn() {
        // Para Apple Sign-In, necesitarías implementar el flujo específico
        // Por ahora, mostramos un mensaje de no implementado
        authViewModel.setError("Apple Sign-In no está implementado aún")
    }
}
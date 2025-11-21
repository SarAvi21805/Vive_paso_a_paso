package com.example.vivepasoapaso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.vivepasoapaso.presentation.navigation.AppNavigation // o SimpleAppNavigation
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import dagger.hilt.android.AndroidEntryPoint
<<<<<<< Updated upstream

//import com.example.vivepasoapaso.util.LocaleManager
=======
>>>>>>> Stashed changes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VivePasoAPasoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
<<<<<<< Updated upstream
                    AppNavigation()
=======
                    AppNavigation() // o SimpleAppNavigation()
>>>>>>> Stashed changes
                }
            }
        }
    }
}
package com.example.vivepasoapaso.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vivepasoapaso.presentation.auth.AuthState
import com.example.vivepasoapaso.ui.screens.dashboard.DashboardScreen
import com.example.vivepasoapaso.ui.screens.login.LoginScreen
import com.example.vivepasoapaso.ui.screens.profile.ProfileScreen
import com.example.vivepasoapaso.ui.screens.progress.ProgressScreen
import com.example.vivepasoapaso.ui.screens.registerhabit.RegisterHabitScreen

@Composable
fun AppNavigation(
    authState: AuthState,
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Authenticated) "dashboard" else "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onGoogleSignIn = onGoogleSignIn,
                onFacebookSignIn = onFacebookSignIn,
                onAppleSignIn = onAppleSignIn
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToProgress = { navController.navigate("progress") },
                onNavigateToRegisterHabit = { navController.navigate("register_habit") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfile = { /* TODO: Navegar a editar perfil */ },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("progress") {
            ProgressScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("register_habit") {
            RegisterHabitScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
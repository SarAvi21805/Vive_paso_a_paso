package com.example.vivepasoapaso.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vivepasoapaso.presentation.auth.AuthViewModel
import com.example.vivepasoapaso.ui.screens.dashboard.DashboardScreen
import com.example.vivepasoapaso.ui.screens.login.LoginScreen
import com.example.vivepasoapaso.ui.screens.profile.ProfileScreen
import com.example.vivepasoapaso.ui.screens.progress.ProgressScreen
import com.example.vivepasoapaso.ui.screens.registerhabit.RegisterHabitScreen
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SimpleAppNavigation(
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    onAppleSignIn: () -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    val currentUser by authViewModel.currentUser.collectAsState()

    // Navegación automática después del login
    LaunchedEffect(currentUser) {
        if (currentUser != null && navController.currentDestination?.route != Screen.Dashboard.route) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) Screen.Dashboard.route else Screen.Login.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToRegisterHabit = { navController.navigate(Screen.RegisterHabit.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoogleSignIn = onGoogleSignIn,
                onFacebookSignIn = onFacebookSignIn,
                onAppleSignIn = onAppleSignIn
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfile = { /* TODO: Implementar edición de perfil */ },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.RegisterHabit.route) {
            RegisterHabitScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
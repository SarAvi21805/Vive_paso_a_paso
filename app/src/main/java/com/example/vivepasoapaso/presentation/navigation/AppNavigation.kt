package com.example.vivepasoapaso.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vivepasoapaso.ui.screens.dashboard.DashboardScreen
import com.example.vivepasoapaso.ui.screens.login.RegisterScreen
import com.example.vivepasoapaso.ui.screens.login.LoginScreen
import com.example.vivepasoapaso.ui.screens.profile.ProfileScreen
import com.example.vivepasoapaso.ui.screens.progress.ProgressScreen
import com.example.vivepasoapaso.ui.screens.registerhabit.RegisterHabitScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Progress : Screen("progress")
    object Profile : Screen("profile")
    object RegisterHabit : Screen("registerHabit")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val startDestination = Screen.Dashboard.route

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToDashboard = {  navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToRegisterHabit = { navController.navigate(Screen.RegisterHabit.route) }
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
                onNavigateToLogin = { navController.navigate(Screen.Login.route)}
            )
        }
        composable(Screen.RegisterHabit.route) {
            RegisterHabitScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
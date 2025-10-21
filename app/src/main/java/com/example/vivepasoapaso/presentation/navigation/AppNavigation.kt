package com.example.vivepasoapaso.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vivepasoapaso.ui.screens.dashboard.DashboardScreen
import com.example.vivepasoapaso.ui.screens.login.LoginScreen
import com.example.vivepasoapaso.ui.screens.profile.ProfileScreen
import com.example.vivepasoapaso.ui.screens.progress.ProgressScreen
import com.example.vivepasoapaso.ui.screens.registerhabit.RegisterHabitScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Progress : Screen("progress")
    object Profile : Screen("profile")

    object RegisterHabit : Screen("registerHabit")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen()
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.RegisterHabit.route) {
            RegisterHabitScreen()
        }
        composable(Screen.Progress.route) {
            ProgressScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
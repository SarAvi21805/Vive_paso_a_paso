package com.example.vivepasoapaso.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Progress : Screen("progress")
    object Profile : Screen("profile")
    object RegisterHabit : Screen("registerHabit")
    object SignUp : Screen("signup")
}
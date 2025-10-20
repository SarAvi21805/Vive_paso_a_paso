package com.example.vivepasoapaso.presentation.screens.progress

data class ProgressState(
    val sleepHours: Double = 7.5,
    val steps: Int = 8000,
    val waterLiters: Double = 1.5,
    val exerciseMinutes: Int = 45,
    val aiRecommendation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
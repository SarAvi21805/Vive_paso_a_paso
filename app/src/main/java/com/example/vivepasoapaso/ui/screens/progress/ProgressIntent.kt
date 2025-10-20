package com.example.vivepasoapaso.presentation.screens.progress

sealed class ProgressIntent {
    object RequestRecommendation : ProgressIntent()
    data class UpdateUserStats(
        val sleepHours: Double,
        val steps: Int,
        val waterLiters: Double,
        val exerciseMinutes: Int
    ) : ProgressIntent()
}
package com.example.vivepasoapaso.presentation.screens.progress

data class ProgressState(
    val sleepHours: Double = 0.0,
    val steps: Int = 0,
    val waterLiters: Double = 0.0,
    val exerciseMinutes: Int = 0,
    val aiRecommendation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val streakDays: Int = 0,
    val totalWeeklySteps: Int = 0,
    val weeklyWaterAverage: Double = 0.0,
    val weeklySleepAverage: Double = 0.0,
    val weeklyExerciseAverage: Double = 0.0
)

/*package com.example.vivepasoapaso.presentation.screens.progress

data class ProgressState(
    val sleepHours: Double = 0.0,
    val steps: Int = 0,
    val waterLiters: Double = 0.0,
    val exerciseMinutes: Int = 0,
    val aiRecommendation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val streakDays: Int = 0,
    val totalWeeklySteps: Int = 0,
    val weeklyWaterAverage: Double = 0.0,
    val weeklySleepAverage: Double = 0.0,
    val weeklyExerciseAverage: Double = 0.0
)*/
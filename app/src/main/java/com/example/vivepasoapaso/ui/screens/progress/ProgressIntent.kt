package com.example.vivepasoapaso.presentation.screens.progress

sealed class ProgressIntent {
    object RequestRecommendation : ProgressIntent()
    object RefreshData : ProgressIntent()
}

/*package com.example.vivepasoapaso.presentation.screens.progress

sealed class ProgressIntent {
    object RequestRecommendation : ProgressIntent()
    data class UpdateUserStats(
        val sleepHours: Double,
        val steps: Int,
        val waterLiters: Double,
        val exerciseMinutes: Int
    ) : ProgressIntent()
    data class LoadWeeklyData(val userId: String) : ProgressIntent()
    data class LoadHabitRecords(val userId: String, val days: Int) : ProgressIntent()
    object RefreshData : ProgressIntent()
}*/
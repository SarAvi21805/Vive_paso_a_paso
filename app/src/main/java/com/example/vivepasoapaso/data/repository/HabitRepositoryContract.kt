package com.example.vivepasoapaso.data.repository

import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.remote.OpenWeatherResponse

interface HabitRepositoryContract {
    suspend fun saveHabitRecord(record: HabitRecord): Result<HabitRecord>

    suspend fun getTodayHabitRecords(userId: String): List<HabitRecord>

    suspend fun getWeeklyStats(userId: String): Map<HabitType, Double>

    suspend fun getHabitRecordsByType(
        userId: String,
        type: HabitType,
        limit: Int = 30
    ): List<HabitRecord>

    suspend fun getCaloriesForFood(foodQuery: String): Double?

    suspend fun getCurrentWeather(lat: Double, lon: Double): OpenWeatherResponse?
}

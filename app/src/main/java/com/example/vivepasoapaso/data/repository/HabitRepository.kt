package com.example.vivepasoapaso.data.repository

import android.util.Log
import com.example.vivepasoapaso.BuildConfig
import com.example.vivepasoapaso.data.local.HabitDao
import com.example.vivepasoapaso.data.local.HabitEntity
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.remote.EdamamApiService
import com.example.vivepasoapaso.data.remote.OpenWeatherApiService
import com.example.vivepasoapaso.data.remote.OpenWeatherResponse
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val edamamApi: EdamamApiService,
    private val openWeatherApi: OpenWeatherApiService,
    private val habitDao: HabitDao,
    private val firestore: FirebaseFirestore
) {

    // Funciones de API's externas
    suspend fun getCaloriesForFood(foodQuery: String): Double? {
        return try {
            val response = edamamApi.getNutritionDetails(
                appId = BuildConfig.EDAMAM_APP_ID,
                appKey = BuildConfig.EDAMAM_APP_KEY,
                food = foodQuery
            )
            response.totalNutrients.calories.quantity
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error fetching calories from Edamam", e)
            null
        }
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): OpenWeatherResponse? {
        return try {
            openWeatherApi.getWeather(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error fetching weather from OpenWeather", e)
            null
        }
    }

    fun getLocalHabitsForDateRange(startDate: Long, endDate: Long): Flow<List<HabitEntity>> {
        return habitDao.getHabitsForUserOnDate(startDate, endDate)
    }

    // Funciones de datos de hábitos

    /**Guarda en Firebase y luego en la BD local.*/
    suspend fun saveHabitRecord(record: HabitRecord): Result<HabitRecord> {
        return try {
            val recordWithId = if (record.id.isEmpty()) {
                record.copy(id = firestore.collection("habits").document().id)
            } else {
                record
            }
            firestore.collection("habits").document(recordWithId.id).set(recordWithId).await()

            // Lógica de BD Local (Room)
            val habitEntity = HabitEntity(
                id = 0, // Room autogenerará el ID local
                name = recordWithId.type.name,
                type = recordWithId.type.name,
                amount = recordWithId.value,
                date = recordWithId.recordDate.toDate().time
            )
            habitDao.insertHabit(habitEntity) // Guardamos en Room

            Result.success(recordWithId)
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error saving habit record", e)
            Result.failure(e)
        }
    }

    suspend fun getHabitRecords(
        userId: String,
        startDate: Date,
        endDate: Date
    ): List<HabitRecord> {
        return try {
            val snapshot = firestore.collection("habits")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("recordDate", Timestamp(startDate))
                .whereLessThanOrEqualTo("recordDate", Timestamp(endDate))
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(HabitRecord::class.java) }
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error getting habit records", e)
            emptyList()
        }
    }

    suspend fun getHabitRecordsByType(
        userId: String,
        type: HabitType,
        limit: Int = 30
    ): List<HabitRecord> {
        return try {
            val snapshot = firestore.collection("habits")
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", type)
                .orderBy("recordDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(HabitRecord::class.java) }
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error getting records by type", e)
            emptyList()
        }
    }

    suspend fun deleteHabitRecord(recordId: String): Result<Unit> {
        return try {
            firestore.collection("habits").document(recordId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error deleting record", e)
            Result.failure(e)
        }
    }

    suspend fun getTodayHabitRecords(userId: String): List<HabitRecord> {
        val calendar = Calendar.getInstance()
        val startOfDay = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time
        val endOfDay = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time
        return getHabitRecords(userId, startOfDay, endOfDay)
    }

    suspend fun getWeeklyStats(userId: String): Map<HabitType, Double> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val weekAgo = calendar.time
        val now = Date()
        val records = getHabitRecords(userId, weekAgo, now)
        return records.groupBy { it.type }
            .mapValues { (_, records) ->
                records.sumOf { it.value } / records.size.coerceAtLeast(1)
            }
    }

    suspend fun getWeeklySummary(userId: String): Map<String, Any> {
        return try {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val startDate = calendar.time

            val records = getHabitRecords(userId, startDate, endDate)

            // Calcular promedios semanales
            val waterRecords = records.filter { it.type == HabitType.WATER }
            val sleepRecords = records.filter { it.type == HabitType.SLEEP }
            val stepsRecords = records.filter { it.type == HabitType.STEPS }
            val exerciseRecords = records.filter { it.type == HabitType.EXERCISE }

            val weeklyStats = mapOf(
                "avgWater" to waterRecords.map { it.value }.average(),
                "avgSleep" to sleepRecords.map { it.value }.average(),
                "totalSteps" to stepsRecords.map { it.value }.sum().toInt(),
                "avgExercise" to exerciseRecords.map { it.value }.average(),
                "streakDays" to calculateStreak(userId)
            )

            weeklyStats
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private suspend fun calculateStreak(userId: String): Int {
        // Implementación simple de racha - contar días consecutivos con al menos un hábito
        var streak = 0
        val calendar = Calendar.getInstance()

        for (i in 0 until 30) { // Revisar hasta 30 días
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val dayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time

            val dayRecords = getHabitRecords(userId, dayStart, dayEnd)
            if (dayRecords.isNotEmpty()) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}
package com.example.vivepasoapaso.data

import android.util.Log
import com.example.vivepasoapaso.BuildConfig // <-- ¡IMPORTANTE! Para acceder a las API Keys
import com.example.vivepasoapaso.data.local.HabitDao
import com.example.vivepasoapaso.data.local.HabitRecord
import com.example.vivepasoapaso.data.remote.EdamamApiService
import com.example.vivepasoapaso.data.remote.OpenWeatherApiService
import com.example.vivepasoapaso.data.remote.OpenWeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val edamamApi: EdamamApiService,
    private val openWeatherApi: OpenWeatherApiService,
    private val habitDao: HabitDao
) {

    /**
     * Llama a la API de Edamam para obtener las calorías de un texto de comida.
     * Devuelve las calorías como un Double, o null si hay un error.
     */
    suspend fun getCaloriesForFood(foodQuery: String): Double? {
        return try {
            val response = edamamApi.getNutritionDetails(
                appId = BuildConfig.EDAMAM_APP_ID,
                appKey = BuildConfig.EDAMAM_APP_KEY,
                food = foodQuery
            )
            // Extrae la cantidad de calorías de la respuesta anidada
            response.totalNutrients.calories.quantity
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error fetching calories from Edamam", e)
            null // Devuelve null para indicar que la llamada falló
        }
    }

    /**
     * Llama a la API de OpenWeather para obtener el clima actual.
     * Devuelve el objeto de respuesta completo, o null si hay un error.
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double): OpenWeatherResponse? {
        return try {
            openWeatherApi.getWeather(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
        } catch (e: Exception) {
            Log.e("HabitRepository", "Error fetching weather from OpenWeather", e)
            null // Devuelve null para indicar que la llamada falló
        }
    }

    /**
     * Inserta un nuevo registro de hábito en la base de datos local.
     */
    suspend fun saveHabitRecord(record: HabitRecord) {
        habitDao.insertHabitRecord(record)
    }

    /**
     * Obtiene todos los registros de hábitos para un usuario en un rango de fechas.
     */
    suspend fun getHabitsForUserOnDate(userId: String, startDate: Long, endDate: Long): List<HabitRecord> {
        return habitDao.getHabitsForUserOnDate(userId, startDate, endDate)
    }
}
package com.example.vivepasoapaso.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

object DataStoreManager {

    //Keys para las preferencias
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_EMAIL = stringPreferencesKey("user_email")
    private val USER_LANGUAGE = stringPreferencesKey("user_language")
    private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

    //Metas diarias
    private val DAILY_WATER_GOAL = doublePreferencesKey("daily_water_goal")
    private val DAILY_SLEEP_GOAL = doublePreferencesKey("daily_sleep_goal")
    private val DAILY_STEPS_GOAL = intPreferencesKey("daily_steps_goal")
    private val DAILY_EXERCISE_GOAL = intPreferencesKey("daily_exercise_goal")
    private val DAILY_CALORIES_GOAL = intPreferencesKey("daily_calories_goal")

    //Guardar datos del usuario
    suspend fun saveUserData(context: Context, name: String, email: String, language: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            preferences[USER_LANGUAGE] = language
        }
    }

    //Obtener datos del usuario
    fun getUserName(context: Context): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[USER_NAME] ?: "Usuario"
        }

    fun getUserEmail(context: Context): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL] ?: "usuario@email.com"
        }

    fun getUserLanguage(context: Context): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[USER_LANGUAGE] ?: "es"
        }

    //Configuración de notificaciones
    suspend fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun getNotificationsEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    //Guardar metas diarias
    suspend fun saveDailyGoals(
        context: Context,
        water: Double,
        sleep: Double,
        steps: Int,
        exercise: Int,
        calories: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_WATER_GOAL] = water
            preferences[DAILY_SLEEP_GOAL] = sleep
            preferences[DAILY_STEPS_GOAL] = steps
            preferences[DAILY_EXERCISE_GOAL] = exercise
            preferences[DAILY_CALORIES_GOAL] = calories
        }
    }

    //Obtener metas diarias
    fun getDailyWaterGoal(context: Context): Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[DAILY_WATER_GOAL] ?: 2.0
        }

    fun getDailySleepGoal(context: Context): Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[DAILY_SLEEP_GOAL] ?: 8.0
        }

    fun getDailyStepsGoal(context: Context): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[DAILY_STEPS_GOAL] ?: 10000
        }

    fun getDailyExerciseGoal(context: Context): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[DAILY_EXERCISE_GOAL] ?: 30
        }

    fun getDailyCaloriesGoal(context: Context): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[DAILY_CALORIES_GOAL] ?: 2000
        }

    //Guardar hábitos (solución simple para datos temporales)
    suspend fun saveHabitRecord(
        context: Context,
        habitType: String,
        value: Double,
        notes: String? = null
    ) {
        val key = stringPreferencesKey("habit_${habitType}_${System.currentTimeMillis()}")
        val record = "$value|${notes ?: ""}"

        context.dataStore.edit { preferences ->
            preferences[key] = record
        }
    }

    //Limpiar todos los datos (para logout)
    suspend fun clearAllData(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
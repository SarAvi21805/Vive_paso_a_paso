package com.example.vivepasoapaso.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

class ProgressViewModel : ViewModel() {
    // private val habitRepository = HabitRepository()  
    // private val authRepository = AuthRepository()

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    private val _weeklyData = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyData: StateFlow<List<DailyStats>> = _weeklyData.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Simular una pequeña demora para mostrar el loading
                kotlinx.coroutines.delay(800)

                // Usar datos por defecto en lugar de Firebase
                _state.value = _state.value.copy(
                    sleepHours = 8.0,
                    steps = 10000,
                    waterLiters = 2.0,
                    exerciseMinutes = 30
                )

                // Cargar datos semanales (datos de ejemplo)
                loadWeeklyData()

                // Generar recomendación
                getAIRecommendation()

                _state.value = _state.value.copy(isLoading = false)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando datos: ${e.message}"
                )
                loadSampleData()
            }
        }
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            try {
                // Usar datos de ejemplo en lugar de Firebase
                val sampleData = generateSampleWeeklyData()
                _weeklyData.value = sampleData

                // Calcular promedios
                calculateWeeklyAverages(sampleData)

            } catch (e: Exception) {
                loadSampleData()
            }
        }
    }

    private fun generateSampleWeeklyData(): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val sampleData = mutableListOf<DailyStats>()

        // Generar datos de ejemplo realistas para los últimos 7 días
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val day = calendar.time

            // Datos de ejemplo realistas con cierta variación
            val water = 1.5 + (Random.nextDouble() * 1.0) // 1.5-2.5 litros
            val sleep = 6.5 + (Random.nextDouble() * 2.0) // 6.5-8.5 horas
            val steps = 8000 + (Random.nextDouble() * 4000).toInt() // 8000-12000 pasos
            val exercise = 20 + (Random.nextDouble() * 40).toInt() // 20-60 minutos

            sampleData.add(DailyStats(
                date = day,
                water = water,
                sleep = sleep,
                steps = steps,
                exercise = exercise,
                nutrition = 1800.0 + (Random.nextDouble() * 400)
            ))
        }

        return sampleData
    }

    private fun loadSampleData() {
        val sampleData = generateSampleWeeklyData()
        _weeklyData.value = sampleData
        calculateWeeklyAverages(sampleData)

        _state.value = _state.value.copy(
            streakDays = 5,
            totalWeeklySteps = 75600,
            weeklyWaterAverage = 1.8,
            weeklySleepAverage = 7.2,
            weeklyExerciseAverage = 35.0,
            aiRecommendation = "¡Buen trabajo! Tu consistencia en el ejercicio está mejorando. Sigue así."
        )
    }

    private fun calculateWeeklyAverages(weeklyStats: List<DailyStats>) {
        if (weeklyStats.isEmpty()) return

        val avgWater = weeklyStats.map { it.water }.average()
        val avgSleep = weeklyStats.map { it.sleep }.average()
        val totalSteps = weeklyStats.map { it.steps }.sum()
        val avgExercise = weeklyStats.map { it.exercise.toDouble() }.average()

        _state.value = _state.value.copy(
            weeklyWaterAverage = avgWater,
            weeklySleepAverage = avgSleep,
            totalWeeklySteps = totalSteps,
            weeklyExerciseAverage = avgExercise,
            streakDays = calculateStreak(weeklyStats)
        )
    }

    private fun calculateStreak(weeklyStats: List<DailyStats>): Int {
        var streak = 0
        for (stats in weeklyStats.reversed()) {
            if (stats.water > 0.5 || stats.sleep > 4 || stats.steps > 1000 || stats.exercise > 10) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    private fun getAIRecommendation() {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(500)

                val recommendations = listOf(
                    "¡Excelente progreso! Tu consumo de agua está mejorando. Sigue hidratándote.",
                    "Tu consistencia en el ejercicio es admirable. Considera agregar variedad a tu rutina.",
                    "Los patrones de sueño muestran mejora. Intenta mantener un horario consistente.",
                    "¡Buen trabajo con los pasos! Prueba caminar al aire libre para variar.",
                    "Tu balance entre ejercicio y descanso es óptimo. Continúa así."
                )

                val randomRecommendation = recommendations.random()

                _state.value = _state.value.copy(
                    aiRecommendation = randomRecommendation
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    aiRecommendation = "Mantén una rutina constante para ver mejoras en tus hábitos."
                )
            }
        }
    }

    fun refreshData() {
        loadUserData()
    }

    fun processIntent(intent: ProgressIntent) {
        when (intent) {
            is ProgressIntent.RequestRecommendation -> {
                getAIRecommendation()
            }
            ProgressIntent.RefreshData -> {
                refreshData()
            }
            else -> {
                // Otros intents pueden manejarse aquí
            }
        }
    }
}

data class DailyStats(
    val date: Date,
    val water: Double,
    val sleep: Double,
    val steps: Int,
    val exercise: Int,
    val nutrition: Double
)

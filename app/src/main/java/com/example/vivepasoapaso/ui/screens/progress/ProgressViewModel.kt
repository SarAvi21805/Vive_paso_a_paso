package com.example.vivepasoapaso.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.repository.HabitRepository
import com.example.vivepasoapaso.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

class ProgressViewModel : ViewModel() {
    private val habitRepository = HabitRepository()
    private val authRepository = AuthRepository()

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
                kotlinx.coroutines.delay(1000)

                val currentUser = authRepository.getCurrentUser()
                currentUser?.let { user ->
                    _state.value = _state.value.copy(
                        sleepHours = user.dailyGoals.sleep,
                        steps = user.dailyGoals.steps,
                        waterLiters = user.dailyGoals.water,
                        exerciseMinutes = user.dailyGoals.exercise
                    )
                } ?: run {
                    // Datos por defecto si no hay usuario
                    _state.value = _state.value.copy(
                        sleepHours = 8.0,
                        steps = 10000,
                        waterLiters = 2.0,
                        exerciseMinutes = 30
                    )
                }

                // Cargar datos semanales (por ahora datos de ejemplo)
                loadWeeklyData()

                // Generar recomendación
                getAIRecommendation()

                _state.value = _state.value.copy(isLoading = false)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando datos: ${e.message}"
                )
                // Cargar datos de ejemplo en caso de error
                loadSampleData()
            }
        }
    }

    private fun loadWeeklyData() {
        viewModelScope.launch {
            try {
                // Por ahora usamos datos de ejemplo
                val sampleData = generateSampleWeeklyData()
                _weeklyData.value = sampleData

                // Calcular promedios
                calculateWeeklyAverages(sampleData)

            } catch (e: Exception) {
                // En caso de error, cargar datos de ejemplo
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
                nutrition = 1800.0 + (Random.nextDouble() * 400) // 1800-2200 calorías
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
        // Calcular racha basada en días consecutivos con al menos un hábito registrado
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
                // Simular procesamiento de IA
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

/*package com.example.vivepasoapaso.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.repository.HabitRepository
import com.example.vivepasoapaso.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ProgressViewModel : ViewModel() {
    private val habitRepository = HabitRepository()
    private val authRepository = AuthRepository()

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    private val _weeklyData = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyData: StateFlow<List<DailyStats>> = _weeklyData.asStateFlow()

    private val _habitRecords = MutableStateFlow<List<HabitRecord>>(emptyList())
    val habitRecords: StateFlow<List<HabitRecord>> = _habitRecords.asStateFlow()

    init {
        loadUserData()
    }

    fun processIntent(intent: ProgressIntent) {
        when (intent) {
            is ProgressIntent.RequestRecommendation -> {
                getAIRecommendation()
            }
            is ProgressIntent.UpdateUserStats -> {
                _state.value = _state.value.copy(
                    sleepHours = intent.sleepHours,
                    steps = intent.steps,
                    waterLiters = intent.waterLiters,
                    exerciseMinutes = intent.exerciseMinutes
                )
                getAIRecommendation()
            }
            is ProgressIntent.LoadWeeklyData -> {
                loadWeeklyData(intent.userId)
            }
            is ProgressIntent.LoadHabitRecords -> {
                loadHabitRecords(intent.userId, intent.days)
            }
            ProgressIntent.RefreshData -> {
                refreshData()
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            currentUser?.let { user ->
                _state.value = _state.value.copy(
                    sleepHours = user.dailyGoals.sleep,
                    steps = user.dailyGoals.steps,
                    waterLiters = user.dailyGoals.water,
                    exerciseMinutes = user.dailyGoals.exercise
                )

                //Cargar datos semanales
                loadWeeklyData(user.id)
                loadHabitRecords(user.id, 7)

                //Generar recomendación basada en datos reales
                getAIRecommendation()
            }
        }
    }

    private fun loadWeeklyData(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                //Obtener registros de la última semana
                val calendar = Calendar.getInstance()
                val endDate = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, -6) //Últimos 7 días
                val startDate = calendar.time

                val records = habitRepository.getHabitRecords(userId, startDate, endDate)
                _habitRecords.value = records

                //Procesar datos para la gráfica semanal
                val weeklyStats = processWeeklyData(records)
                _weeklyData.value = weeklyStats

                //Actualizar estadísticas actuales
                updateCurrentStats(records)

                //Calcular promedios semanales
                calculateWeeklyAverages(weeklyStats)

                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    private fun loadHabitRecords(userId: String, days: Int) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -(days - 1))
            val startDate = calendar.time

            val records = habitRepository.getHabitRecords(userId, startDate, endDate)
            _habitRecords.value = records
        }
    }

    private fun processWeeklyData(records: List<HabitRecord>): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val weeklyStats = mutableListOf<DailyStats>()

        //Para cada día de la última semana
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val day = calendar.time

            //Filtrar registros de este día
            val dayRecords = records.filter { record ->
                val recordDate = Date(record.recordDate.seconds * 1000)
                isSameDay(recordDate, day)
            }

            //Calcular métricas para el día
            val dailyStats = DailyStats(
                date = day,
                water = dayRecords.find { it.type == HabitType.WATER }?.value ?: 0.0,
                sleep = dayRecords.find { it.type == HabitType.SLEEP }?.value ?: 0.0,
                steps = dayRecords.find { it.type == HabitType.STEPS }?.value?.toInt() ?: 0,
                exercise = dayRecords.find { it.type == HabitType.EXERCISE }?.value?.toInt() ?: 0,
                nutrition = dayRecords.find { it.type == HabitType.NUTRITION }?.value ?: 0.0
            )

            weeklyStats.add(dailyStats)
        }

        return weeklyStats
    }

    private fun updateCurrentStats(records: List<HabitRecord>) {
        val today = Date()
        val todayRecords = records.filter { record ->
            val recordDate = Date(record.recordDate.seconds * 1000)
            isSameDay(recordDate, today)
        }

        val currentState = _state.value
        _state.value = currentState.copy(
            waterLiters = todayRecords.find { it.type == HabitType.WATER }?.value ?: currentState.waterLiters,
            sleepHours = todayRecords.find { it.type == HabitType.SLEEP }?.value ?: currentState.sleepHours,
            steps = todayRecords.find { it.type == HabitType.STEPS }?.value?.toInt() ?: currentState.steps,
            exerciseMinutes = todayRecords.find { it.type == HabitType.EXERCISE }?.value?.toInt() ?: currentState.exerciseMinutes
        )
    }

    private fun calculateWeeklyAverages(weeklyStats: List<DailyStats>) {
        val avgWater = weeklyStats.map { it.water }.average()
        val avgSleep = weeklyStats.map { it.sleep }.average()
        val totalSteps = weeklyStats.map { it.steps }.sum()
        val avgExercise = weeklyStats.map { it.exercise.toDouble() }.average()

        _state.value = _state.value.copy(
            weeklyWaterAverage = avgWater,
            weeklySleepAverage = avgSleep,
            totalWeeklySteps = totalSteps,
            weeklyExerciseAverage = avgExercise,
            streakDays = 5 // Valor temporal
        )
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getAIRecommendation() {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val userStats = buildUserStatsPrompt()
                //Respuesta simulada
                val recommendation = "Toma agua cada vez que sientas sed y mantente activo."

                _state.value = _state.value.copy(
                    aiRecommendation = recommendation,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al obtener recomendación: ${e.message}",
                    isLoading = false,
                    aiRecommendation = "No se pudo conectar con el servicio de IA. Verifica tu conexión a internet."
                )
            }
        }
    }

    private fun buildUserStatsPrompt(): String {
        val state = _state.value
        val weeklyStats = _weeklyData.value

        val avgWater = weeklyStats.map { it.water }.average()
        val avgSleep = weeklyStats.map { it.sleep }.average()
        val avgSteps = weeklyStats.map { it.steps.toDouble() }.average()
        val avgExercise = weeklyStats.map { it.exercise.toDouble() }.average()

        return """
            Usuario con los siguientes hábitos semanales:
            - Sueño: ${"%.1f".format(avgSleep)} horas promedio (objetivo: ${state.sleepHours}h)
            - Pasos: ${"%.0f".format(avgSteps)} pasos promedio (objetivo: ${state.steps})
            - Agua: ${"%.1f".format(avgWater)}L promedio (objetivo: ${state.waterLiters}L)
            - Ejercicio: ${"%.0f".format(avgExercise)}min promedio (objetivo: ${state.exerciseMinutes}min)
            
            Proporciona una recomendación personalizada breve (máximo 2 oraciones) para mejorar su bienestar. 
            Compara sus promedios semanales con sus objetivos y sugiere ajustes específicos.
            Sé específico y sugiere un hábito pequeño y alcanzable.
        """.trimIndent()
    }

    fun refreshData() {
        loadUserData()
    }
}

data class DailyStats(
    val date: Date,
    val water: Double,
    val sleep: Double,
    val steps: Int,
    val exercise: Int,
    val nutrition: Double
)*/
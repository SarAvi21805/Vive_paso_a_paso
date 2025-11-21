package com.example.vivepasoapaso.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.repository.ChatRepository
import com.example.vivepasoapaso.data.repository.ChatRepositoryImpl
import com.example.vivepasoapaso.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {
    private val chatRepository: ChatRepository = ChatRepositoryImpl()

    //Estados para filtros
    private val _selectedPeriod = MutableStateFlow("weekly") // "weekly" o "monthly"
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    private val _selectedHabitFilter = MutableStateFlow("all") // "all", "water", "sleep", "steps", "exercise"
    val selectedHabitFilter: StateFlow<String> = _selectedHabitFilter.asStateFlow()

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    //Datos para gráficas
    private val _weeklyData = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyData: StateFlow<List<DailyStats>> = _weeklyData.asStateFlow()

    private val _monthlyData = MutableStateFlow<List<DailyStats>>(emptyList())
    val monthlyData: StateFlow<List<DailyStats>> = _monthlyData.asStateFlow()

    //Datos procesados para gráficas
    private val _chartData = MutableStateFlow(ChartData())
    val chartData: StateFlow<ChartData> = _chartData.asStateFlow()

    //Colores para cada hábito
    private val habitColors = mapOf(
        "water" to 0xFF2196F3,    //Azul
        "sleep" to 0xFF9C27B0,    //Morado
        "steps" to 0xFFFF9800,    //Naranja
        "exercise" to 0xFFF44336, //Rojo
        "nutrition" to 0xFF4CAF50 //Verde
    )

    init {
        loadUserData()
    }

    fun setPeriod(period: String) {
        _selectedPeriod.value = period
        refreshData()
        updateChartData()
    }

    fun setHabitFilter(habit: String) {
        _selectedHabitFilter.value = habit
        updateChartData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val userId = "current_user_id" // Reemplazar con ID real del usuario

                // Cargar datos reales desde Firebase
                loadWeeklyData(userId)
                loadMonthlyData(userId)
                getAIRecommendation()

                _state.value = _state.value.copy(isLoading = false)
                updateChartData()

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando datos: ${e.message}"
                )
                loadSampleData()
            }
        }
    }

    private suspend fun loadWeeklyData(userId: String) {
        try {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val startDate = calendar.time

            val records = habitRepository.getHabitRecords(userId, startDate, endDate)
            val weeklyStats = processRecordsToDailyStats(records, true)
            _weeklyData.value = weeklyStats
            calculateWeeklyAverages(weeklyStats)

            // Calcular racha real
            val streak = calculateRealStreak(userId)
            _state.value = _state.value.copy(streakDays = streak)

        } catch (e: Exception) {
            loadSampleData()
        }
    }

    private suspend fun loadMonthlyData(userId: String) {
        try {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -29)
            val startDate = calendar.time

            val records = habitRepository.getHabitRecords(userId, startDate, endDate)
            val monthlyStats = processRecordsToDailyStats(records, false)
            _monthlyData.value = monthlyStats
        } catch (e: Exception) {
            // Manejar error
        }
    }

    private fun processRecordsToDailyStats(records: List<com.example.vivepasoapaso.data.model.HabitRecord>, isWeekly: Boolean): List<DailyStats> {
        val groupedByDay = records.groupBy { record ->
            val calendar = Calendar.getInstance()
            calendar.time = record.recordDate.toDate()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }

        val dailyStatsList = mutableListOf<DailyStats>()
        val dateFormat = if (isWeekly) SimpleDateFormat("EEE", Locale.getDefault()) else SimpleDateFormat("dd/MM", Locale.getDefault())

        groupedByDay.forEach { (date, recordsForDay) ->
            val dayLabel = if (isWeekly) dateFormat.format(date).take(3) else dateFormat.format(date)

            var water = 0.0
            var sleep = 0.0
            var steps = 0
            var exercise = 0
            var nutrition = 0.0

            recordsForDay.forEach { record ->
                when (record.type) {
                    com.example.vivepasoapaso.data.model.HabitType.WATER -> water += record.value
                    com.example.vivepasoapaso.data.model.HabitType.SLEEP -> sleep += record.value
                    com.example.vivepasoapaso.data.model.HabitType.STEPS -> steps += record.value.toInt()
                    com.example.vivepasoapaso.data.model.HabitType.EXERCISE -> exercise += record.value.toInt()
                    com.example.vivepasoapaso.data.model.HabitType.NUTRITION -> nutrition += record.value
                    else -> {}
                }
            }

            dailyStatsList.add(DailyStats(
                date = date,
                dayLabel = dayLabel,
                water = water,
                sleep = sleep,
                steps = steps,
                exercise = exercise,
                nutrition = nutrition
            ))
        }

        return dailyStatsList.sortedBy { it.date }
    }

    private suspend fun calculateRealStreak(userId: String): Int {
        var streak = 0
        val calendar = Calendar.getInstance()

        // Verificar días consecutivos con al menos un hábito registrado
        for (i in 0 until 30) {
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

            val dayRecords = habitRepository.getHabitRecords(userId, dayStart, dayEnd)
            if (dayRecords.isNotEmpty()) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    private fun generateSampleWeeklyData(): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val sampleData = mutableListOf<DailyStats>()
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val day = calendar.time

            //Datos más realistas para gráficas semanales
            val water = 1.5 + (kotlin.random.Random.nextDouble() * 1.0)
            val sleep = 6.5 + (kotlin.random.Random.nextDouble() * 2.0)
            val steps = 8000 + (kotlin.random.Random.nextDouble() * 4000).toInt()
            val exercise = 20 + (kotlin.random.Random.nextDouble() * 40).toInt()

            sampleData.add(DailyStats(
                date = day,
                dayLabel = dateFormat.format(day).take(3), //"Lun", "Mar", etc.
                water = water,
                sleep = sleep,
                steps = steps,
                exercise = exercise,
                nutrition = 1800.0 + (kotlin.random.Random.nextDouble() * 400)
            ))
        }

        return sampleData
    }

    private fun generateSampleMonthlyData(): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val sampleData = mutableListOf<DailyStats>()

        //Generar datos para 4 semanas
        for (week in 0 until 4) {
            calendar.time = Date()
            calendar.add(Calendar.WEEK_OF_YEAR, -week)

            //Promedio semanal para cada hábito
            val baseWater = 1.5 + (week * 0.1)
            val baseSleep = 6.5 + (week * 0.2)
            val baseSteps = 8000 + (week * 500)
            val baseExercise = 20 + (week * 5)

            val water = baseWater + (kotlin.random.Random.nextDouble() * 0.3)
            val sleep = baseSleep + (kotlin.random.Random.nextDouble() * 0.5)
            val steps = baseSteps + (kotlin.random.Random.nextDouble() * 1000).toInt()
            val exercise = baseExercise + (kotlin.random.Random.nextDouble() * 10).toInt()

            sampleData.add(DailyStats(
                date = calendar.time,
                dayLabel = "${week + 1}", //"1", "2", "3", "4" para semanas
                water = water,
                sleep = sleep,
                steps = steps,
                exercise = exercise,
                nutrition = 1800.0 + (Random.nextDouble() * 200)
            ))
        }

        return sampleData.reversed() //Ordenar de la semana 1 a la 4
    }

    //Función clave: procesar datos para gráficas
    private fun updateChartData() {
        val currentData = if (_selectedPeriod.value == "weekly") {
            _weeklyData.value
        } else {
            _monthlyData.value
        }

        val selectedHabit = _selectedHabitFilter.value

        val chartData = if (selectedHabit == "all") {
            //Modo múltiples hábitos - Gráfica de líneas (como la primera imagen)
            processMultiHabitLineData(currentData)
        } else {
            //Modo hábito individual - Gráfica de barras (como la segunda imagen)
            processSingleHabitBarData(currentData, selectedHabit)
        }

        _chartData.value = chartData
    }

    private fun processSingleHabitBarData(data: List<DailyStats>, habit: String): ChartData {
        val labels = data.map { it.dayLabel }
        val values = when (habit) {
            "water" -> data.map { it.water }
            "sleep" -> data.map { it.sleep }
            "steps" -> data.map { it.steps.toFloat() / 1000 } //Convertir a miles para mejor visualización
            "exercise" -> data.map { it.exercise.toFloat() }
            "nutrition" -> data.map { it.nutrition / 1000 } //Convertir a miles de calorías
            else -> data.map { it.water }
        }

        val color = habitColors[habit] ?: 0xFF2196F3

        return ChartData(
            labels = labels,
            datasets = listOf(
                ChartDataset(
                    label = getHabitDisplayName(habit),
                    values = values,
                    color = color,
                    chartType = if (habit == "steps" || habit == "exercise") "bar" else "bar"
                )
            ),
            isMultiHabit = false,
            yAxisConfig = when (habit) {
                "steps" -> YAxisConfig(
                    minValue = 0f,
                    maxValue = 12f, //Miles de pasos
                    labelFormat = "{value}K",
                    steps = 6
                )
                "exercise" -> YAxisConfig(
                    minValue = 0f,
                    maxValue = 60f,
                    labelFormat = "{value}m",
                    steps = 6
                )
                "water" -> YAxisConfig(
                    minValue = 0f,
                    maxValue = 3f,
                    labelFormat = "{value}L",
                    steps = 6
                )
                "sleep" -> YAxisConfig(
                    minValue = 0f,
                    maxValue = 10f,
                    labelFormat = "{value}h",
                    steps = 5
                )
                else -> YAxisConfig(
                    minValue = 0f,
                    maxValue = 2.5f,
                    labelFormat = "{value}K",
                    steps = 5
                )
            }
        )
    }

    private fun processMultiHabitLineData(data: List<DailyStats>): ChartData {
        val labels = data.map { it.dayLabel }

        //Crear datasets para cada hábito (líneas de diferentes colores)
        val datasets = listOf(
            ChartDataset(
                label = "Agua",
                values = data.map { it.water },
                color = habitColors["water"] ?: 0xFF2196F3,
                chartType = "line"
            ),
            ChartDataset(
                label = "Sueño",
                values = data.map { it.sleep },
                color = habitColors["sleep"] ?: 0xFF4CAF50,
                chartType = "line"
            ),
            ChartDataset(
                label = "Pasos",
                values = data.map { it.steps.toFloat() / 1000 }, // Convertir a miles
                color = habitColors["steps"] ?: 0xFFFF9800,
                chartType = "line"
            ),
            ChartDataset(
                label = "Ejercicio",
                values = data.map { it.exercise.toFloat() },
                color = habitColors["exercise"] ?: 0xFFF44336,
                chartType = "line"
            )
        )

        return ChartData(
            labels = labels,
            datasets = datasets,
            isMultiHabit = true,
            yAxisConfig = YAxisConfig(
                minValue = 0f,
                maxValue = 10f,
                labelFormat = "{value}",
                steps = 5
            )
        )
    }

    private fun getHabitDisplayName(habit: String): String {
        return when (habit) {
            "water" -> "Consumo de Agua (L)"
            "sleep" -> "Horas de Sueño"
            "steps" -> "Pasos (miles)"
            "exercise" -> "Ejercicio (min)"
            "nutrition" -> "Calorías (miles)"
            else -> "Hábito"
        }
    }

    private fun loadSampleData() {
        val sampleData = generateSampleWeeklyData()
        _weeklyData.value = sampleData
        calculateWeeklyAverages(sampleData)
        updateChartData()

        _state.value = _state.value.copy(
            streakDays = 5,
            totalWeeklySteps = 75600,
            weeklyWaterAverage = 1.8,
            weeklySleepAverage = 7.2,
            weeklyExerciseAverage = 35.0
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

    fun getAIRecommendation() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val currentLanguage = Locale.getDefault().language
                val userStats = buildUserStatsMessage(currentLanguage)
                val response = chatRepository.getAIResponse(userStats)

                _state.value = _state.value.copy(
                    aiRecommendation = response.text,
                    isLoading = false
                )

            } catch (e: Exception) {
                val currentLanguage = Locale.getDefault().language
                val errorRecommendation = if (currentLanguage == "es") {
                    "Mantén una rutina constante para ver mejoras en tus hábitos. Intenta beber más agua y mantener un horario de sueño regular."
                } else {
                    "Keep a consistent routine to see improvements in your habits. Try to drink more water and maintain a regular sleep schedule."
                }

                _state.value = _state.value.copy(
                    aiRecommendation = errorRecommendation,
                    isLoading = false
                )
            }
        }
    }

    private fun buildUserStatsMessage(language: String): String {
        val state = _state.value

        return if (language == "es") {
            "Soy un usuario de una app de hábitos saludables. " +
                    "Mis estadísticas semanales son: " +
                    "Promedio de sueño: ${String.format("%.1f", state.weeklySleepAverage)} horas, " +
                    "Promedio de agua: ${String.format("%.1f", state.weeklyWaterAverage)} litros, " +
                    "Promedio de ejercicio: ${String.format("%.1f", state.weeklyExerciseAverage)} minutos, " +
                    "Total de pasos: ${state.totalWeeklySteps}. " +
                    "Mi objetivo diario es: ${state.sleepHours} horas de sueño, ${state.waterLiters} litros de agua, ${state.exerciseMinutes} minutos de ejercicio y ${state.steps} pasos. " +
                    "Por favor, dame una recomendación personalizada, breve y motivadora basada en mi progreso. " +
                    "Sé positivo y alentador. Responde en español."
        } else {
            "I'm a user of a healthy habits app. " +
                    "My weekly stats are: " +
                    "Sleep average: ${String.format("%.1f", state.weeklySleepAverage)} hours, " +
                    "Water average: ${String.format("%.1f", state.weeklyWaterAverage)} liters, " +
                    "Exercise average: ${String.format("%.1f", state.weeklyExerciseAverage)} minutes, " +
                    "Total steps: ${state.totalWeeklySteps}. " +
                    "My daily goal is: ${state.sleepHours} hours of sleep, ${state.waterLiters} liters of water, ${state.exerciseMinutes} minutes of exercise and ${state.steps} steps. " +
                    "Please give me a personalized, brief and motivating recommendation based on my progress. " +
                    "Be positive and encouraging. Respond in English."
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
        }
    }
}

//Data classes para gráficas
data class DailyStats(
    val date: Date,
    val dayLabel: String, //Etiqueta para el eje X ("Lun", "Mar", "1", "2", etc.)
    val water: Double,
    val sleep: Double,
    val steps: Int,
    val exercise: Int,
    val nutrition: Double
)

data class ChartData(
    val labels: List<String> = emptyList(), //Etiquetas del eje X
    val datasets: List<ChartDataset> = emptyList(),
    val isMultiHabit: Boolean = false,
    val yAxisConfig: YAxisConfig = YAxisConfig()
)

data class ChartDataset(
    val label: String,
    val values: List<Any>, //Valores numéricos
    val color: Long,
    val chartType: String = "line" //"line" o "bar"
)

data class YAxisConfig(
    val minValue: Float = 0f,
    val maxValue: Float = 10f,
    val labelFormat: String = "{value}",
    val steps: Int = 5
)

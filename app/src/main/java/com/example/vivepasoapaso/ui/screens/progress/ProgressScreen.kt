package com.example.vivepasoapaso.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.presentation.screens.progress.ProgressIntent
import com.example.vivepasoapaso.presentation.screens.progress.ProgressViewModel
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit = {}
) {
    val viewModel: ProgressViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val weeklyData by viewModel.weeklyData.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.your_progress),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar datos")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar loading si está cargando
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando tus datos...")
                    }
                }
            } else {
                // Mostrar error si existe
                state.error?.let { error ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refreshData() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                    return@Column
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                // Botones de filtro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    FilterButton(
                        text = stringResource(id = R.string.weekly),
                        isSelected = true,
                        onClick = { viewModel.refreshData() },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = stringResource(id = R.string.all_habits),
                        isSelected = false,
                        onClick = { /* TODO: Implementar filtro */ },
                        modifier = Modifier.weight(1f),
                        showDropdown = true
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                // Gráfico de barras con datos
                WeeklyBarChart(weeklyData = weeklyData)

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                // Recomendación de IA
                AIRecommendationCard(
                    recommendation = state.aiRecommendation,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRefresh = { viewModel.processIntent(ProgressIntent.RequestRecommendation) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                // Tarjeta con la racha
                StreakCard(streakDays = state.streakDays)

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                // Estadísticas
                StatsGrid(
                    weeklySleepAverage = state.weeklySleepAverage,
                    sleepGoal = state.sleepHours,
                    totalWeeklySteps = state.totalWeeklySteps,
                    stepsGoal = state.steps,
                    weeklyWaterAverage = state.weeklyWaterAverage,
                    waterGoal = state.waterLiters,
                    weeklyExerciseAverage = state.weeklyExerciseAverage,
                    exerciseGoal = state.exerciseMinutes.toDouble()
                )
            }
        }
    }
}

// Componente para botones de filtro
@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDropdown: Boolean = false
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = text)
            if (showDropdown) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = text)
            if (showDropdown) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
    }
}

// Gráfico de barras semanal
@Composable
fun WeeklyBarChart(weeklyData: List<com.example.vivepasoapaso.presentation.screens.progress.DailyStats>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Consumo de Agua - Últimos 7 días",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (weeklyData.isNotEmpty()) {
                val maxWater = (weeklyData.maxOfOrNull { it.water } ?: 1.0).coerceAtLeast(1.0)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEachIndexed { index, dailyStat ->
                        val dayLabel = when (index) {
                            0 -> "D"
                            1 -> "L"
                            2 -> "M"
                            3 -> "M"
                            4 -> "J"
                            5 -> "V"
                            6 -> "S"
                            else -> ""
                        }

                        val heightFraction = (dailyStat.water / maxWater).toFloat().coerceIn(0f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Valor numérico
                            Text(
                                text = "%.1f".format(dailyStat.water),
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Barra
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(heightFraction)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.primary)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Día de la semana
                            Text(
                                text = dayLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Mostrar mensaje si no hay datos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay datos registrados",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Registra tus hábitos para ver tu progreso",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta de racha
@Composable
fun StreakCard(streakDays: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            Column {
                Text(
                    text = "¡Racha de $streakDays días!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Sigue así para desbloquear un logro")
            }
        }
    }
}

// Grid de estadísticas
@Composable
fun StatsGrid(
    weeklySleepAverage: Double,
    sleepGoal: Double,
    totalWeeklySteps: Int,
    stepsGoal: Int,
    weeklyWaterAverage: Double,
    waterGoal: Double,
    weeklyExerciseAverage: Double,
    exerciseGoal: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.margin_medium))
    ) {
        // Primera fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            StatCard(
                title = "Promedio de sueño",
                currentValue = weeklySleepAverage,
                targetValue = sleepGoal,
                unit = "h",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Pasos Totales",
                currentValue = totalWeeklySteps.toDouble(),
                targetValue = stepsGoal.toDouble(),
                unit = "",
                modifier = Modifier.weight(1f)
            )
        }

        // Segunda fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            StatCard(
                title = "Agua Promedio",
                currentValue = weeklyWaterAverage,
                targetValue = waterGoal,
                unit = "L",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Ejercicio Promedio",
                currentValue = weeklyExerciseAverage,
                targetValue = exerciseGoal,
                unit = "min",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Tarjeta de estadística reutilizable
@Composable
fun StatCard(
    title: String,
    currentValue: Double,
    targetValue: Double,
    unit: String,
    modifier: Modifier = Modifier
) {
    val progress = if (targetValue > 0) (currentValue / targetValue).coerceIn(0.0, 1.0) else 0.0
    val isGoalAchieved = currentValue >= targetValue

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (unit.isEmpty()) {
                    "${currentValue.toInt()}"
                } else {
                    "%.1f$unit".format(currentValue)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isGoalAchieved) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Barra de progreso
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isGoalAchieved) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Objetivo: ${if (unit.isEmpty()) targetValue.toInt() else "%.1f$unit".format(targetValue)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Recomendación de IA
@Composable
fun AIRecommendationCard(
    recommendation: String,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recomendación de Vita",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar recomendación"
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

            if (isLoading) {
                Column {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        text = "Vita está analizando tus hábitos...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (!error.isNullOrEmpty()) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = recommendation.ifEmpty { "Presiona el botón de actualizar para obtener una recomendación personalizada." },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    VivePasoAPasoTheme {
        ProgressScreen()
    }
}

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
)*/

/*package com.example.vivepasoapaso.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme

// VERSIÓN DIAGNÓSTICO - Pantalla minimalista para probar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreenDiagnostic(
    onBackClick: () -> Unit = {}
) {
    // 1. Primero probemos sin ViewModel
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diagnóstico Progress",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elementos básicos para ver si la pantalla se renderiza
            Text(
                text = "🚨 PANTALLA DE DIAGNÓSTICO 🚨",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Si ves este texto, la pantalla se está renderizando",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Test button */ }
            ) {
                Text("Botón de prueba")
            }

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Problemas comunes:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "1. ViewModel no se instancia\n2. Navegación incorrecta\n3. Tema no aplicado\n4. Errores en composables",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// VERSIÓN SIMPLIFICADA del ProgressScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreenSimple(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.your_progress),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Datos hardcodeados para prueba
            Text("Tu Progreso", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Agua: 1.5L / 2.0L", style = MaterialTheme.typography.bodyLarge)
                    Text("Sueño: 7h / 8h", style = MaterialTheme.typography.bodyLarge)
                    Text("Pasos: 8500 / 10000", style = MaterialTheme.typography.bodyLarge)
                    Text("Ejercicio: 25min / 30min", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Datos")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenDiagnosticPreview() {
    VivePasoAPasoTheme {
        ProgressScreenDiagnostic()
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenSimplePreview() {
    VivePasoAPasoTheme {
        ProgressScreenSimple()
    }
}*/

/*package com.example.vivepasoapaso.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.presentation.screens.progress.ProgressIntent
import com.example.vivepasoapaso.presentation.screens.progress.ProgressViewModel
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit = {}
) {
    val viewModel: ProgressViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val weeklyData by viewModel.weeklyData.collectAsState()

    //Cargar datos reales al iniciar
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.your_progress),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar datos")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Mostrar loading si está cargando
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando tus datos...")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                //Botones de filtro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    Button(onClick = {
                        //Recargar datos semanales
                        viewModel.refreshData()
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.weekly))
                    }
                    OutlinedButton(onClick = { /* Filtro de gráfica */ }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.all_habits))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                //Gráfico de barras con datos reales de Firebase
                WeeklyBarChart(weeklyData = weeklyData)

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                //Recomendación de IA
                AIRecommendationCard(
                    recommendation = state.aiRecommendation,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRefresh = { viewModel.processIntent(ProgressIntent.RequestRecommendation) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                //Parte de la tarjeta con la racha
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
                        Column {
                            Text(
                                text = stringResource(id = R.string.streak_title, state.streakDays),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = stringResource(id = R.string.streak_subtitle))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                //Primera fila de estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    //Sección de promedio de sueño
                    StatCard(
                        title = stringResource(id = R.string.avg_sleep),
                        currentValue = state.weeklySleepAverage,
                        targetValue = state.sleepHours,
                        unit = "h",
                        modifier = Modifier.weight(1f)
                    )
                    //Sección de total de pasos semanales
                    StatCard(
                        title = "Pasos Totales",
                        currentValue = state.totalWeeklySteps.toDouble(),
                        targetValue = state.steps.toDouble(),
                        unit = "",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                //Segunda fila de estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    //Sección de agua
                    StatCard(
                        title = "Agua Promedio",
                        currentValue = state.weeklyWaterAverage,
                        targetValue = state.waterLiters,
                        unit = "L",
                        modifier = Modifier.weight(1f)
                    )
                    //Sección de ejercicio
                    StatCard(
                        title = "Ejercicio Promedio",
                        currentValue = state.weeklyExerciseAverage,
                        targetValue = state.exerciseMinutes.toDouble(),
                        unit = "min",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

//Gráfico de barras semanal con datos reales
@Composable
fun WeeklyBarChart(weeklyData: List<com.example.vivepasoapaso.presentation.screens.progress.DailyStats>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Consumo de Agua - Últimos 7 días",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (weeklyData.isNotEmpty()) {
                //Encontrar el valor máximo para escalar la gráfica
                val maxWater = (weeklyData.maxOfOrNull { it.water } ?: 1.0).coerceAtLeast(1.0)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEachIndexed { index, dailyStat ->
                        val dayLabel = when (index) {
                            0 -> "D"
                            1 -> "L"
                            2 -> "M"
                            3 -> "M"
                            4 -> "J"
                            5 -> "V"
                            6 -> "S"
                            else -> ""
                        }

                        val heightFraction = (dailyStat.water / maxWater).toFloat().coerceIn(0f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            //Valor numérico
                            Text(
                                text = "%.1f".format(dailyStat.water),
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            //Barra
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(heightFraction)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.primary)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            //Día de la semana
                            Text(
                                text = dayLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                //Mostrar mensaje si no hay datos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay datos registrados",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Registra tus hábitos para ver tu progreso",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

//Tarjeta de estadística reutilizable
@Composable
fun StatCard(
    title: String,
    currentValue: Double,
    targetValue: Double,
    unit: String,
    modifier: Modifier = Modifier
) {
    val progress = if (targetValue > 0) (currentValue / targetValue).coerceIn(0.0, 1.0) else 0.0
    val isGoalAchieved = currentValue >= targetValue

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (unit.isEmpty()) {
                    "${currentValue.toInt()}"
                } else {
                    "%.1f$unit".format(currentValue)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isGoalAchieved) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            //Barra de progreso
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isGoalAchieved) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Objetivo: ${if (unit.isEmpty()) targetValue.toInt() else "%.1f$unit".format(targetValue)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

//Recomendación de IA
@Composable
fun AIRecommendationCard(
    recommendation: String,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recomendación de Vita",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar recomendación"
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

            if (isLoading) {
                Column {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        text = "Vita está analizando tus hábitos...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (!error.isNullOrEmpty()) {
                Text(
                    text = "$error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = recommendation.ifEmpty { "Presiona el botón de actualizar para obtener una recomendación personalizada." },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    VivePasoAPasoTheme {
        ProgressScreen()
    }
}*/
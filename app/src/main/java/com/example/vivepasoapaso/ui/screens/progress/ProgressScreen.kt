package com.example.vivepasoapaso.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit = {},
    viewModel: ProgressViewModel = hiltViewModel()
) {
    //val viewModel: ProgressViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val weeklyData by viewModel.weeklyData.collectAsState()
    val monthlyData by viewModel.monthlyData.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedHabitFilter by viewModel.selectedHabitFilter.collectAsState()

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
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    //Selector de período
                    var periodExpanded by remember { mutableStateOf(false) }
                    val periods = listOf(
                        "weekly" to stringResource(id = R.string.weekly),
                        "monthly" to stringResource(id = R.string.monthly)
                    )

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedButton(
                            onClick = { periodExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(periods.first { it.first == selectedPeriod }.second)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = periodExpanded,
                            onDismissRequest = { periodExpanded = false }
                        ) {
                            periods.forEach { period ->
                                DropdownMenuItem(
                                    text = { Text(period.second) },
                                    onClick = {
                                        viewModel.setPeriod(period.first)
                                        periodExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    //Selector de hábitos
                    var habitExpanded by remember { mutableStateOf(false) }
                    val habits = listOf(
                        "all" to stringResource(id = R.string.all_habits),
                        "water" to stringResource(id = R.string.hydration),
                        "sleep" to stringResource(id = R.string.sleep),
                        "exercise" to stringResource(id = R.string.exercise),
                        "steps" to stringResource(id = R.string.steps)
                    )

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedButton(
                            onClick = { habitExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(habits.first { it.first == selectedHabitFilter }.second)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = habitExpanded,
                            onDismissRequest = { habitExpanded = false }
                        ) {
                            habits.forEach { habit ->
                                DropdownMenuItem(
                                    text = { Text(habit.second) },
                                    onClick = {
                                        viewModel.setHabitFilter(habit.first)
                                        habitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                //Gráfico de barras
                val currentData = if (selectedPeriod == "weekly") weeklyData else monthlyData
                if (currentData.isNotEmpty()) {
                    HabitBarChart(
                        data = currentData,
                        selectedPeriod = selectedPeriod,
                        selectedHabit = selectedHabitFilter
                    )
                } else {
                    //Mensaje cuando no hay datos
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(id = R.string.no_data_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.no_data_message),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                //Recomendación de IA
                AIRecommendationCard(
                    recommendation = state.aiRecommendation,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRefresh = { viewModel.processIntent(ProgressIntent.RequestRecommendation) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                //Tarjeta de racha
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
                            Text(text = stringResource(id = R.string.streak_subtitle, state.streakDays))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                //Estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    StatCard(
                        title = stringResource(id = R.string.avg_sleep),
                        currentValue = state.weeklySleepAverage,
                        targetValue = state.sleepHours,
                        unit = "h",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(id = R.string.total_steps),
                        currentValue = state.totalWeeklySteps.toDouble(),
                        targetValue = state.steps.toDouble(),
                        unit = "",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    StatCard(
                        title = stringResource(id = R.string.avg_water),
                        currentValue = state.weeklyWaterAverage,
                        targetValue = state.waterLiters,
                        unit = "L",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(id = R.string.avg_exercise),
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

@Composable
fun HabitBarChart(
    data: List<com.example.vivepasoapaso.presentation.screens.progress.DailyStats>,
    selectedPeriod: String,
    selectedHabit: String
) {
    val chartHeight = if (selectedPeriod == "weekly") 220.dp else 280.dp
    val barWidth = if (selectedPeriod == "weekly") 20.dp else 10.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = when {
                    selectedHabit == "all" -> stringResource(if (selectedPeriod == "weekly") R.string.all_habits_weekly else R.string.all_habits_monthly)
                    selectedHabit == "water" -> stringResource(if (selectedPeriod == "weekly") R.string.water_weekly else R.string.water_monthly)
                    selectedHabit == "sleep" -> stringResource(if (selectedPeriod == "weekly") R.string.sleep_weekly else R.string.sleep_monthly)
                    selectedHabit == "exercise" -> stringResource(if (selectedPeriod == "weekly") R.string.exercise_weekly else R.string.exercise_monthly)
                    else -> stringResource(if (selectedPeriod == "weekly") R.string.steps_weekly else R.string.steps_monthly)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (data.isNotEmpty()) {
                //Obtener valores según el hábito seleccionado
                val values = data.map { dailyStat ->
                    when (selectedHabit) {
                        "water" -> dailyStat.water
                        "sleep" -> dailyStat.sleep
                        "exercise" -> dailyStat.exercise.toDouble()
                        "steps" -> dailyStat.steps.toDouble()
                        else -> (dailyStat.water + dailyStat.sleep + dailyStat.exercise / 60.0) / 3.0 // Promedio normalizado para "all"
                    }
                }

                val maxValue = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
                val chartHeightDp = if (selectedPeriod == "weekly") 120.dp else 180.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeightDp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        data.forEachIndexed { index, dailyStat ->
                            val value = values[index]
                            val heightFraction = (value / maxValue).toFloat().coerceIn(0.1f, 1f)

                            val label = if (selectedPeriod == "weekly") {
                                val days = listOf("D", "L", "M", "M", "J", "V", "S")
                                days.getOrNull(index) ?: "${index + 1}"
                            } else {
                                "${index + 1}"
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                            ) {
                                //Valor numérico
                                Text(
                                    text = when {
                                        selectedHabit == "steps" -> "${value.toInt()}"
                                        selectedHabit == "exercise" -> "${value.toInt()}"
                                        else -> "%.1f".format(value)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                //Barra
                                Box(
                                    modifier = Modifier
                                        .width(barWidth)
                                        .height((chartHeightDp * 0.7f) * heightFraction)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(
                                            when (selectedHabit) {
                                                "water" -> Color(0xFF2196F3)
                                                "sleep" -> Color(0xFF4CAF50)
                                                "exercise" -> Color(0xFFFF9800)
                                                "steps" -> Color(0xFF9C27B0)
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                //Etiqueta del día
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.no_data_title),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(id = R.string.no_data_message),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

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
                    text = stringResource(id = R.string.ai_recommendation_title),
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
}
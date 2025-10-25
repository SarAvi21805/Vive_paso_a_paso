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
}
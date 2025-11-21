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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.presentation.screens.progress.ProgressViewModel
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
<<<<<<< Updated upstream
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color
=======
>>>>>>> Stashed changes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit = {}
) {
<<<<<<< Updated upstream
    val viewModel: ProgressViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val weeklyData by viewModel.weeklyData.collectAsState()
    val monthlyData by viewModel.monthlyData.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedHabitFilter by viewModel.selectedHabitFilter.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
=======
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val selectedHabitFilter by viewModel.selectedHabitFilter.collectAsStateWithLifecycle()
>>>>>>> Stashed changes

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

                // Selectores de filtro
                FilterSection(
                    selectedPeriod = selectedPeriod,
                    selectedHabitFilter = selectedHabitFilter,
                    onPeriodSelected = { period -> viewModel.setPeriod(period) },
                    onHabitSelected = { habit -> viewModel.setHabitFilter(habit) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                // Gráfico simple
                SimpleProgressChart()

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                // Recomendación de IA
                SimpleAIRecommendationCard(
                    recommendation = state.aiRecommendation,
                    isLoading = state.isLoading,
                    onRefresh = { viewModel.processIntent(com.example.vivepasoapaso.presentation.screens.progress.ProgressIntent.RequestRecommendation) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                // Tarjeta de racha
                StreakCard(streakDays = state.streakDays)

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                // Estadísticas
                SimpleStatsGrid(state = state)
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedPeriod: String,
    selectedHabitFilter: String,
    onPeriodSelected: (String) -> Unit,
    onHabitSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        // Selector de período
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            modifier = Modifier.weight(1f)
        )

        // Selector de hábitos
        HabitFilterSelector(
            selectedHabit = selectedHabitFilter,
            onHabitSelected = onHabitSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val periods = listOf(
        "weekly" to stringResource(id = R.string.weekly),
        "monthly" to stringResource(id = R.string.monthly)
    )

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(periods.first { it.first == selectedPeriod }.second)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.second) },
                    onClick = {
                        onPeriodSelected(period.first)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun HabitFilterSelector(
    selectedHabit: String,
    onHabitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val habits = listOf(
        "all" to stringResource(id = R.string.all_habits),
        "water" to stringResource(id = R.string.hydration),
        "sleep" to stringResource(id = R.string.sleep),
        "exercise" to stringResource(id = R.string.exercise),
        "steps" to stringResource(id = R.string.steps)
    )

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(habits.first { it.first == selectedHabit }.second)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            habits.forEach { habit ->
                DropdownMenuItem(
                    text = { Text(habit.second) },
                    onClick = {
                        onHabitSelected(habit.first)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SimpleProgressChart() {
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
                    text = "Gráfico de Progreso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Aquí se mostrarán tus estadísticas gráficas")
                Text("(En desarrollo)")
            }
        }
    }
}

@Composable
fun SimpleAIRecommendationCard(
    recommendation: String,
    isLoading: Boolean,
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
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        text = "Vita está analizando tus hábitos...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = recommendation.ifEmpty { "Presiona el botón de actualizar para obtener una recomendación personalizada." },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

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
                    text = stringResource(id = R.string.streak_title, streakDays),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(id = R.string.streak_subtitle))
            }
        }
    }
}

@Composable
fun SimpleStatsGrid(state: com.example.vivepasoapaso.presentation.screens.progress.ProgressState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        SimpleStatCard(
            title = stringResource(id = R.string.avg_sleep),
            value = state.weeklySleepAverage,
            unit = "h",
            modifier = Modifier.weight(1f)
        )
        SimpleStatCard(
            title = stringResource(id = R.string.total_steps),
            value = state.totalWeeklySteps.toDouble(),
            unit = "",
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        SimpleStatCard(
            title = stringResource(id = R.string.avg_water),
            value = state.weeklyWaterAverage,
            unit = "L",
            modifier = Modifier.weight(1f)
        )
        SimpleStatCard(
            title = stringResource(id = R.string.avg_exercise),
            value = state.weeklyExerciseAverage,
            unit = "min",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SimpleStatCard(
    title: String,
    value: Double,
    unit: String,
    modifier: Modifier = Modifier
) {
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
                    "${value.toInt()}"
                } else {
                    "%.1f$unit".format(value)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
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
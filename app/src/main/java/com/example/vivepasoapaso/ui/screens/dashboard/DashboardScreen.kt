package com.example.vivepasoapaso.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vivepasoapaso.presentation.habits.HabitViewModel
import com.example.vivepasoapaso.util.ImageManager
import com.example.vivepasoapaso.data.model.HabitRecord
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProgress: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRegisterHabit: () -> Unit = {},
    viewModel: HabitViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val habitViewModel: HabitViewModel = viewModel()
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    // Estado para los hábitos de hoy
    var todayHabits by remember { mutableStateOf<List<HabitRecord>>(emptyList()) }
    var weatherTip by remember { mutableStateOf("") }
    val profileImage = remember { ImageManager.loadProfileImage(context) }

    LaunchedEffect(Unit) {
        // Cargar hábitos de hoy
        currentUser?.uid?.let { userId ->
            habitViewModel.loadTodayRecords(userId)
        }
        // Cargar consejo del clima
        weatherTip = habitViewModel.loadWeatherTip(context)
    }

    // Observar cambios en los registros
    val todayRecords by habitViewModel.todayRecords.collectAsState()
    todayHabits = todayRecords

    Scaffold(
        topBar = {
            TopGreetingBar(
                userName = currentUser?.displayName ?: "Usuario",
                profileImage = profileImage,
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRegisterHabit,
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Hábito")
            }
        },
        bottomBar = {
            BottomNavBar(
                onNavigateToDashboard = { /* Ya estamos en dashboard */ },
                onNavigateToProgress = onNavigateToProgress,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            // Mostrar mensaje si no hay hábitos hoy
            if (todayHabits.isEmpty()) {
                EncouragementCard(onAddHabit = onNavigateToRegisterHabit)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
            }

            DailyTipCard(weatherTip = weatherTip)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))
            HabitGrid(todayHabits = todayHabits)
        }
    }
}

@Composable
fun TopGreetingBar(userName: String, profileImage: android.graphics.Bitmap?, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium),
                vertical = dimensionResource(id = R.dimen.padding_large)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.greeting, userName),
            style = MaterialTheme.typography.headlineMedium
        )
        // Círculo del perfil con imagen clickeable
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            if (profileImage != null) {
                Image(
                    bitmap = profileImage.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EncouragementCard(onAddHabit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                text = "¡Mantén tu racha!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Registra tus hábitos de hoy para continuar con tu progreso.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onAddHabit) {
                Text("Registrar hábitos ahora")
            }
        }
    }
}

@Composable
fun DailyTipCard(weatherTip: String) {
    val tip = if (weatherTip.isNotEmpty()) {
        weatherTip
    } else {
        stringResource(id = R.string.daily_tip_content)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
            Text(
                text = stringResource(id = R.string.daily_tip_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_small)))
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun HabitGrid(todayHabits: List<HabitRecord>) {
    // Calcular progresos basados en los hábitos de hoy
    val waterProgress = calculateProgress(todayHabits, "WATER")
    val sleepProgress = calculateProgress(todayHabits, "SLEEP")
    val exerciseProgress = calculateProgress(todayHabits, "EXERCISE")
    val nutritionProgress = calculateProgress(todayHabits, "NUTRITION")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            HabitCard(
                title = stringResource(id = R.string.hydration),
                progressText = waterProgress,
                icon = Icons.Default.WaterDrop
            )
            HabitCard(
                title = stringResource(id = R.string.nutrition),
                progressText = nutritionProgress,
                icon = Icons.Default.Restaurant
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            HabitCard(
                title = stringResource(id = R.string.sleep),
                progressText = sleepProgress,
                icon = Icons.Default.Bedtime
            )
            HabitCard(
                title = stringResource(id = R.string.steps),
                progressText = exerciseProgress,
                icon = Icons.Default.DirectionsWalk
            )
        }
    }
}

private fun calculateProgress(habits: List<HabitRecord>, type: String): String {
    val typeHabits = habits.filter { it.type.name == type }
    return when (type) {
        "WATER" -> {
            val total = typeHabits.sumOf { it.value }
            "${String.format("%.1f", total)} / 2.0 Lts"
        }
        "SLEEP" -> {
            val total = typeHabits.sumOf { it.value }
            "${String.format("%.1f", total)}h"
        }
        "EXERCISE" -> {
            val total = typeHabits.sumOf { it.value }
            "${total.toInt()} min"
        }
        "NUTRITION" -> {
            val total = typeHabits.sumOf { it.value }
            "${total.toInt()} kcal"
        }
        else -> "0"
    }
}

@Composable
fun HabitCard(title: String, progressText: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp), // Altura fija
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium)),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(text = progressText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    onNavigateToDashboard: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        // Dashboard "seleccionado" (por ahora)
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                onNavigateToDashboard()
            },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(id = R.string.dashboard_nav)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                onNavigateToProgress()
            },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text(stringResource(id = R.string.stats_nav)) }
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                onNavigateToProfile()
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text(stringResource(id = R.string.profile_nav)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    VivePasoAPasoTheme {
        DashboardScreen()
    }
}
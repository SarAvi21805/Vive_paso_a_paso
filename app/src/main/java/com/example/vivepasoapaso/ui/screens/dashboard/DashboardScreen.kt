package com.example.vivepasoapaso.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.presentation.dashboard.DashboardViewModel
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.example.vivepasoapaso.util.ImageManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProgress: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRegisterHabit: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val state by viewModel.state.collectAsState()

    // Cargar datos solo si hay usuario autenticado
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            viewModel.loadUserData(userId)
        }
    }

    Scaffold(
        topBar = {
            TopGreetingBar(
                userName = currentUser?.displayName ?: "Invitado",
                profileImage = ImageManager.loadProfileImage(context),
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            // Solo mostrar FAB si hay usuario autenticado
            if (currentUser != null) {
                FloatingActionButton(
                    onClick = onNavigateToRegisterHabit,
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Hábito")
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                onNavigateToDashboard = { /* Ya estamos en dashboard */ },
                onNavigateToProgress = onNavigateToProgress,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToLogin = onNavigateToLogin
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

            // Cargar datos del usuario inmediatamente
            LaunchedEffect(currentUser) {
                currentUser?.uid?.let { userId ->
                    viewModel.loadUserData(userId)
                }
            }

            // Mostrar mensaje si no hay usuario
            if (currentUser == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡Bienvenido a Vive Paso a Paso!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Inicia sesión para comenzar a registrar tus hábitos y ver tu progreso.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToLogin) {
                            Text("Iniciar sesión")
                        }
                    }
                }
            } else {
                // Contenido normal cuando hay usuario autenticado
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando tus hábitos...")
                        }
                    }
                } else {
                    if (state.todayHabits.isEmpty()) {
                        EncouragementCard(onAddHabit = onNavigateToRegisterHabit)
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
                    }

                    state.error?.let { error ->
                        ErrorCard(error = error)
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
                    }

                    DailyTipCard(weatherTip = state.weatherTip)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))
                    HabitGrid(todayHabits = state.todayHabits)
                }
            }
        }
    }
}

@Composable
fun TopGreetingBar(
    userName: String,
    profileImage: android.graphics.Bitmap?,
    onProfileClick: () -> Unit
) {
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
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            if (profileImage != null) {
                androidx.compose.foundation.Image(
                    bitmap = profileImage.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun HabitGrid(todayHabits: List<com.example.vivepasoapaso.data.model.HabitRecord>) {
    // Calcular progresos basados en los hábitos de hoy
    val (waterProgress, waterValue) = calculateProgress(todayHabits, "WATER")
    val (sleepProgress, sleepValue) = calculateProgress(todayHabits, "SLEEP")
    val (exerciseProgress, exerciseValue) = calculateProgress(todayHabits, "EXERCISE")
    val (nutritionProgress, nutritionValue) = calculateProgress(todayHabits, "NUTRITION")

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
                currentValue = waterValue,
                targetValue = 2.0,
                unit = "L",
                icon = Icons.Default.WaterDrop
            )
            HabitCard(
                title = stringResource(id = R.string.nutrition),
                progressText = nutritionProgress,
                currentValue = nutritionValue,
                targetValue = 2000.0,
                unit = "kcal",
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
                currentValue = sleepValue,
                targetValue = 8.0,
                unit = "h",
                icon = Icons.Default.Bedtime
            )
            HabitCard(
                title = stringResource(id = R.string.steps),
                progressText = exerciseProgress,
                currentValue = exerciseValue,
                targetValue = 10000.0,
                unit = "pasos",
                icon = Icons.Default.DirectionsWalk
            )
        }
    }
}

private fun calculateProgress(habits: List<com.example.vivepasoapaso.data.model.HabitRecord>, type: String): Pair<String, Double> {
    val typeHabits = habits.filter { it.type.name == type }
    return when (type) {
        "WATER" -> {
            val total = typeHabits.sumOf { it.value }
            "${String.format("%.1f", total)} / 2.0 L" to total
        }
        "SLEEP" -> {
            val total = typeHabits.sumOf { it.value }
            "${String.format("%.1f", total)}h" to total
        }
        "EXERCISE" -> {
            val total = typeHabits.sumOf { it.value }
            "${total.toInt()} min" to total
        }
        "NUTRITION" -> {
            val total = typeHabits.sumOf { it.value }
            "${total.toInt()} kcal" to total
        }
        "STEPS" -> {
            val total = typeHabits.sumOf { it.value }
            "${total.toInt()} pasos" to total
        }
        else -> "0" to 0.0
    }
}

@Composable
fun HabitCard(
    title: String,
    progressText: String,
    currentValue: Double,
    targetValue: Double,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val progress = if (targetValue > 0) (currentValue / targetValue).coerceIn(0.0, 1.0) else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Column {
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progreso
                LinearProgressIndicator(
                    progress = progress.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Objetivo: $targetValue$unit",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    onNavigateToDashboard: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                onNavigateToDashboard()
            },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(id = R.string.dashboard_nav)) }
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
package com.example.vivepasoapaso.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.example.vivepasoapaso.util.ImageManager
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay

// Componente principal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProgress: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRegisterHabit: () -> Unit = {}
) {
    val context = LocalContext.current
    val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: IllegalStateException) {
        null
    }

    val currentUser = auth?.currentUser
    val userName = currentUser?.displayName ?: "Usuario"

    // Estado para la imagen de perfil con remember
    val profileImage by remember {
        mutableStateOf<Bitmap?>(ImageManager.loadProfileImage(context))
    }

    // Estado para los últimos hábitos registrados
    var recentHabits by remember { mutableStateOf<List<HabitRecord>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Simular carga de datos
        delay(500)
        recentHabits = listOf(
            HabitRecord("Ejercicio", "45 min", "Hoy", Icons.Default.DirectionsWalk),
            HabitRecord("Agua", "1.5 L", "Hoy", Icons.Default.WaterDrop),
            HabitRecord("Sueño", "7.2 h", "Ayer", Icons.Default.Bedtime),
            HabitRecord("Alimentación", "1800 cal", "Ayer", Icons.Default.Restaurant)
        )
    }

    Scaffold(
        topBar = { TopGreetingBar(userName, profileImage) },
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

            // Mensaje motivacional
            MotivationCard(
                onRegisterHabit = onNavigateToRegisterHabit
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            // Tip del día
            DailyTipCard()

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            // Últimos hábitos registrados
            RecentHabitsSection(
                habits = recentHabits,
                onRegisterHabit = onNavigateToRegisterHabit
            )
        }
    }
}

@Composable
fun TopGreetingBar(userName: String, profileImage: Bitmap?) {
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

        // Círculo del perfil con imagen o inicial
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
            // Mostrar inicial si no hay imagen
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MotivationCard(
    onRegisterHabit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                text = "¡Es hora de registrar tus hábitos!",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
            Text(
                text = "Completa tu día registrando tus hábitos actuales para mantener tu progreso",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
            Button(
                onClick = onRegisterHabit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Hábitos de Hoy")
            }
        }
    }
}

@Composable
fun DailyTipCard() {
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
                text = stringResource(id = R.string.daily_tip_content),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RecentHabitsSection(
    habits: List<HabitRecord>,
    onRegisterHabit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tus últimos hábitos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onRegisterHabit) {
                Text("Ver todos")
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        if (habits.isEmpty()) {
            // Mensaje cuando no hay hábitos registrados
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay hábitos registrados",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRegisterHabit) {
                            Text("Registrar primer hábito")
                        }
                    }
                }
            }
        } else {
            // Lista de hábitos recientes
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                habits.forEach { habit ->
                    RecentHabitCard(habit = habit)
                }
            }
        }
    }
}

@Composable
fun RecentHabitCard(habit: HabitRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = habit.icon,
                contentDescription = habit.type,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.type,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = habit.value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = habit.date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

// Data class para representar un hábito registrado
data class HabitRecord(
    val type: String,
    val value: String,
    val date: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    VivePasoAPasoTheme {
        DashboardScreen()
    }
}
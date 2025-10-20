package com.example.vivepasoapaso.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
// Para inconos de las tarjetas de hábitos
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop

// Para inconos de la barra de navegación
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import androidx.compose.material3.NavigationBarItemDefaults

// Componente principal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    Scaffold(
        topBar = { TopGreetingBar("Julían") },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* No action */ },
                containerColor = MaterialTheme.colorScheme.tertiary) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Hábito")
            }
        },
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        // Contenedor principal del contenido de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding para no superponerse con las barras
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
            DailyTipCard()
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))
            HabitGrid()
        }
    }
}

// Componentes pequeños y reutilizables

// Para la barra superior con el saludo y círculo del perfil
@Composable
fun TopGreetingBar(userName: String){
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
        // Círculo del perfil
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    }
}

// Para la tarjeta con el tip diario
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

// Organización de las 4 tarjetas de hábitos con cuadrícula 2x2
@Composable
fun HabitGrid() {
    // Usamos una Fila que contiene dos Columnas para crear la cuadrícula
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // Columna Izquierda
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            HabitCard(
                title = stringResource(id = R.string.hydration),
                progressText = "1,5 / 1,5 Lts",
                icon = Icons.Default.WaterDrop
            )
            HabitCard(
                title = stringResource(id = R.string.nutrition),
                progressText = "1,200 / 2,000 kca",
                icon = Icons.Default.Restaurant
            )
        }
        // Columna Derecha
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            HabitCard(
                title = stringResource(id = R.string.sleep),
                progressText = "7h 30m",
                icon = Icons.Default.Bedtime
            )
            HabitCard(
                title = stringResource(id = R.string.steps),
                progressText = "1,200 / 2,000",
                icon = Icons.Default.DirectionsWalk
            )
        }
    }
}

// Tarjeta individual (reutilizable) para mostrar el progreso de un hábito
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

// Para la barra de navegación inferior
@Composable
fun BottomNavBar() {
    NavigationBar {
        // Dashboard "seleccionado" (por ahora)
        NavigationBarItem(
            selected = true,
            onClick = { /* No action */ },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(id = R.string.dashboard_nav)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* No action */ },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text(stringResource(id = R.string.stats_nav)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* No action */ },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text(stringResource(id = R.string.profile_nav)) }
        )
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    VivePasoAPasoTheme {
        DashboardScreen()
    }
}
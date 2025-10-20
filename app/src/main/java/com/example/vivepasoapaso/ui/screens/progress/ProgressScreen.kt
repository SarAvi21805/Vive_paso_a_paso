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

    //Solicitar recomendación al cargar la pantalla
    LaunchedEffect(Unit) {
        viewModel.processIntent(ProgressIntent.RequestRecommendation)
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
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            //Botones de filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                Button(onClick = { /* Cambio de elección */ }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.weekly))
                }
                OutlinedButton(onClick = { /* Filtro de gráfica */ }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.all_habits))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Gráfico de barras simulado
            BarChart()

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Recomendación de IA
            AIRecommendationCard(
                recommendation = state.aiRecommendation,
                isLoading = state.isLoading,
                error = state.error,
                onRefresh = { viewModel.processIntent(ProgressIntent.RequestRecommendation) }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Parte de la tarjeta con la racha, pasos y tiempo de sueño en promedio
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
                            text = stringResource(id = R.string.streak_title, 5),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = stringResource(id = R.string.streak_subtitle))
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                //Sección de promedio de sueño
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                        Text(text = stringResource(id = R.string.avg_sleep))
                        Text(
                            text = "${state.sleepHours}h",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                //Sección de promedio de pasos
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                        Text(text = stringResource(id = R.string.total_steps))
                        Text(
                            text = "${state.steps}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            //Sección de Agua y Ejercicio
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                //Sección de agua
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                        Text(text = "Agua")
                        Text(
                            text = "${state.waterLiters}L",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                //Sección de ejercicio
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                        Text(text = "Ejercicio")
                        Text(
                            text = "${state.exerciseMinutes}min",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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

@Composable
fun BarChart() {
    //Representación de la gráfica (ejemplo por ahora)
    val barValues = listOf(0.4f, 0.6f, 0.5f, 0.8f, 0.9f, 0.7f, 0.5f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        barValues.forEach { value ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(value)
                    .padding(horizontal = 4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary)
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
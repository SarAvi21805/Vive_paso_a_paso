package com.example.vivepasoapaso.ui.screens.registerhabit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.screens.dashboard.BottomNavBar
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterHabitScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedHabit by remember { mutableStateOf("Ejercicio") }
    var value by remember { mutableStateOf(TextFieldValue()) }
    var notes by remember { mutableStateOf(TextFieldValue()) }
    var mood by remember { mutableStateOf("Normal") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val moods = listOf("Feliz", "Triste", "Enojado", "Estresado", "Irritable", "Relajado", "Normal")
    val habits = listOf("Agua", "Sueño", "Ejercicio", "Alimentación", "Meditación")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar Hábito",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                onNavigateToDashboard = onBackClick,
                onNavigateToProgress = { /* */ },
                onNavigateToProfile = { /* */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de fecha
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    showDatePicker = false
                },
                showDatePicker = showDatePicker,
                onShowDatePickerChanged = { showDatePicker = it }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            // Selector de hábitos
            HabitScrollableSelector(
                selectedHabit = selectedHabit,
                onHabitSelected = { selectedHabit = it },
                habits = habits
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            // Campo de valor según el hábito seleccionado
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = when (selectedHabit) {
                        "Ejercicio" -> "Minutos de ejercicio"
                        "Agua" -> "Litros de agua"
                        "Sueño" -> "Horas de sueño"
                        "Alimentación" -> "Calorías consumidas"
                        "Meditación" -> "Minutos de meditación"
                        else -> "Valor"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        // Validación básica según el tipo de hábito
                        when (selectedHabit) {
                            "Ejercicio", "Meditación" -> {
                                // Solo números para minutos
                                if (newValue.text.all { it.isDigit() } || newValue.text.isEmpty()) {
                                    value = newValue
                                }
                            }
                            "Agua", "Sueño" -> {
                                // Números y punto decimal
                                if (newValue.text.matches(Regex("^\\d*\\.?\\d*$")) || newValue.text.isEmpty()) {
                                    value = newValue
                                }
                            }
                            "Alimentación" -> {
                                // Solo números para calorías
                                if (newValue.text.all { it.isDigit() } || newValue.text.isEmpty()) {
                                    value = newValue
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            // Selector de estado de ánimo
            Text(
                text = "Estado de ánimo",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

            MoodSelector(
                selectedMood = mood,
                onMoodSelected = { mood = it },
                moods = moods
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            // Notas
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Notas adicionales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    singleLine = false,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Button(
                    onClick = {
                        // Guardar lógica aquí
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = value.text.isNotEmpty()
                ) {
                    Text(text = "Guardar")
                }
                OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f)) {
                    Text(text = "Cancelar")
                }
            }
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    showDatePicker: Boolean,
    onShowDatePickerChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Fecha del hábito",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para seleccionar fecha
        OutlinedButton(
            onClick = { onShowDatePickerChanged(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatDate(selectedDate),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Selector de fecha simple
        if (showDatePicker) {
            SimpleDatePicker(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                onDismiss = { onShowDatePickerChanged(false) }
            )
        }
    }
}

@Composable
fun SimpleDatePicker(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Seleccionar fecha",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val calendar = Calendar.getInstance()
            calendar.time = selectedDate

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Día
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Día", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${calendar.get(Calendar.DAY_OF_MONTH)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row {
                        IconButton(
                            onClick = {
                                calendar.add(Calendar.DAY_OF_MONTH, -1)
                                onDateSelected(calendar.time)
                            }
                        ) {
                            Text("−")
                        }
                        IconButton(
                            onClick = {
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                                onDateSelected(calendar.time)
                            }
                        ) {
                            Text("+")
                        }
                    }
                }

                // Mes
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mes", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${calendar.get(Calendar.MONTH) + 1}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row {
                        IconButton(
                            onClick = {
                                calendar.add(Calendar.MONTH, -1)
                                onDateSelected(calendar.time)
                            }
                        ) {
                            Text("−")
                        }
                        IconButton(
                            onClick = {
                                calendar.add(Calendar.MONTH, 1)
                                onDateSelected(calendar.time)
                            }
                        ) {
                            Text("+")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Aceptar")
            }
        }
    }
}

@Composable
fun HabitScrollableSelector(
    selectedHabit: String,
    onHabitSelected: (String) -> Unit,
    habits: List<String>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        items(habits) { habit ->
            val isSelected = habit == selectedHabit

            if (isSelected) {
                Button(onClick = { onHabitSelected(habit) }) {
                    Text(text = habit)
                }
            } else {
                OutlinedButton(onClick = { onHabitSelected(habit) }) {
                    Text(text = habit)
                }
            }
        }
    }
}

@Composable
fun MoodSelector(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    moods: List<String>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        items(moods) { mood ->
            val isSelected = mood == selectedMood

            if (isSelected) {
                Button(
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Default.Mood, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = mood)
                }
            } else {
                OutlinedButton(
                    onClick = { onMoodSelected(mood) },
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Default.Mood, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = mood)
                }
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun RegisterHabitScreenPreview() {
    VivePasoAPasoTheme {
        RegisterHabitScreen()
    }
}
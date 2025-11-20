package com.example.vivepasoapaso.ui.screens.registerhabit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.presentation.habits.HabitViewModel
import com.example.vivepasoapaso.ui.screens.dashboard.BottomNavBar
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.google.firebase.Timestamp
import com.example.vivepasoapaso.ui.screens.dashboard.BottomNavBar
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.*

// Definición de los tipos de hábito como enum
enum class HabitOption(val displayName: String) {
    WATER("Agua"),
    SLEEP("Sueño"),
    EXERCISE("Ejercicio"),
    FOOD("Alimentación")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterHabitScreen(
    onBackClick: () -> Unit = {}, viewModel: HabitViewModel = hiltViewModel()
) {
    // Estados para manejar el formulario dinámico
    var selectedHabit by remember { mutableStateOf(HabitOption.EXERCISE) }
    var primaryInputValue by remember { mutableStateOf("") } // Para minutos, litros, etc.
    var notesValue by remember { mutableStateOf("") }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.margin_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
            Text(
                text = stringResource(id = R.string.register_habit_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(id = R.string.date_label),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            HabitScrollableSelector(
                selectedHabit = selectedHabit,
                onHabitSelected = {
                    selectedHabit = it
                    // Limpieza de campos al cambia de hábito
                    primaryInputValue = ""
                    notesValue = ""
                }
            )

            // Formulario dinámico
            DynamicHabitInput(
                habit = selectedHabit,
                primaryValue = primaryInputValue,
                onPrimaryValueChange = { primaryInputValue = it },
                notesValue = notesValue,
                onNotesValueChange = { notesValue = it }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            /*
            OutlinedTextField(
                value = "45",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.minutes_of_exercise)) },
                modifier = Modifier.fillMaxWidth()
            )*/
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                // Botón de guardar
                Button(
                    onClick = {
                        // ViewModel se encarga de la lógica compleja
                        val userId = "XXXXXX" // Reemplazar con el ID del usuario

                        if (selectedHabit == HabitOption.FOOD) {
                            // Si es comida, llamada a la función de la API
                            viewModel.calculateAndSaveFoodHabit(
                                foodDescription = primaryInputValue,
                                userId = userId
                            )
                        } else {
                            // Para otros hábitos, creación del record directamente
                            val record = HabitRecord(
                                userId = userId,
                                type = HabitType.valueOf(selectedHabit.name), // Conversión de enum a HabitType
                                value = primaryInputValue.toDoubleOrNull() ?: 0.0,
                                notes = notesValue,
                                recordDate = Timestamp.now()
                            )
                            viewModel.saveHabitRecord(record)
                        }
                        onBackClick()},
                    modifier = Modifier.weight(1f),
                    enabled = primaryInputValue.isNotBlank() // El botón se activa solo si hay un valor
                ) {
                    Text(text = stringResource(id = R.string.save_button))
                }
                OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
            }
        }
    }
}
        /*    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

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
}*/

@Composable
fun HabitScrollableSelector(
    selectedHabit: HabitOption,
    onHabitSelected: (HabitOption) -> Unit
) {
    val habits = HabitOption.values()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
    ) {
        items(habits) { habit ->
            val isSelected = habit == selectedHabit
            Button(
                onClick = { onHabitSelected(habit) },
                colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(text = habit.displayName)
            }
        }
    }
}

@Composable
fun DynamicHabitInput(
    habit: HabitOption,
    primaryValue: String,
    onPrimaryValueChange: (String) -> Unit,
    notesValue: String,
    onNotesValueChange: (String) -> Unit
) {
    // Este Composable muestra el campo de texto correcto según el hábito seleccionado
    when (habit) {
        HabitOption.EXERCISE -> {
            OutlinedTextField(
                value = primaryValue,
                onValueChange = onPrimaryValueChange,
                label = { Text("Minutos de ejercicio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        HabitOption.WATER -> {
            OutlinedTextField(
                value = primaryValue,
                onValueChange = onPrimaryValueChange,
                label = { Text("Litros de agua") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        HabitOption.SLEEP -> {
            OutlinedTextField(
                value = primaryValue,
                onValueChange = onPrimaryValueChange,
                label = { Text("Horas de sueño") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        HabitOption.FOOD -> {
            OutlinedTextField(
                value = primaryValue,
                onValueChange = onPrimaryValueChange,
                label = { Text("¿Qué comiste? (ej: '1 apple and 1 coffee')") }, // ¡El campo para la API!
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    // El campo de notas es común para todos
    OutlinedTextField(
        value = notesValue,
        onValueChange = onNotesValueChange,
        label = { Text(stringResource(id = R.string.notes_optional)) },
        modifier = Modifier.fillMaxWidth()
    )
}

/*@Composable
fun HabitScrollableSelector() {
    // El estado funciona exactamente igual que antes
    var selectedHabit by remember { mutableStateOf("Ejercicio") }
    val habits = listOf("Agua", "Sueño", "Ejercicio", "Alimentación", "Meditar", "Leer") // Hábitos

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
}*/

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
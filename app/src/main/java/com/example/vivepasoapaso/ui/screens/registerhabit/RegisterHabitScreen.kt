package com.example.vivepasoapaso.ui.screens.registerhabit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.model.MoodOption
import com.example.vivepasoapaso.presentation.habits.HabitViewModel
import com.example.vivepasoapaso.ui.screens.dashboard.BottomNavBar
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.google.firebase.Timestamp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.*

// Definición de los tipos de hábito como enum
enum class HabitOption(val displayName: String) {
    WATER("Agua"),
    SLEEP("Sueño"),
    EXERCISE("Ejercicio"),
    NUTRITION("Alimentación")
}

// Función auxiliar para obtener unidades según el tipo de hábito
private fun getUnitForHabitType(habitType: HabitType): String {
    return when (habitType) {
        HabitType.WATER -> "L"
        HabitType.SLEEP -> "hrs"
        HabitType.EXERCISE -> "min"
        HabitType.STEPS -> "steps"
        HabitType.NUTRITION -> "cal"
        HabitType.MEDITATION -> "min"
        HabitType.READING -> "min"
    }
}

// Función auxiliar para mapear HabitOption a HabitType
private fun mapHabitOptionToType(habitOption: HabitOption): HabitType {
    return when (habitOption) {
        HabitOption.WATER -> HabitType.WATER
        HabitOption.SLEEP -> HabitType.SLEEP
        HabitOption.EXERCISE -> HabitType.EXERCISE
        HabitOption.NUTRITION -> HabitType.NUTRITION
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterHabitScreen(
    onBackClick: () -> Unit = {},
    viewModel: HabitViewModel = hiltViewModel()
) {
    // Estados para manejar el formulario dinámico
    var selectedHabit by remember { mutableStateOf(HabitOption.EXERCISE) }
    var primaryInputValue by remember { mutableStateOf("") }
    var notesValue by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(MoodOption.NORMAL) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.register_habit_title),
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
                onNavigateToProgress = { /* No needed here */ },
                onNavigateToProfile = { /* No needed here */ },
                onNavigateToLogin = { /* No needed here */ }
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

            // Selector de fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(id = R.string.date_label)}: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                }
            }

            if (showDatePicker) {
                // Implementación básica de selector de fecha
                AlertDialog(
                    onDismissRequest = { showDatePicker = false },
                    title = { Text("Seleccionar fecha") },
                    text = {
                        // En una implementación real, usarías DatePicker de Material3
                        Text("Selecciona una fecha para el registro")
                    },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Aceptar")
                        }
                    }
                )
            }

            HabitScrollableSelector(
                selectedHabit = selectedHabit,
                onHabitSelected = {
                    selectedHabit = it
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

            // Selector de estado de ánimo
            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Button(
                    onClick = {
                        val userId = "current_user_id" // Reemplazar con ID real del usuario

                        if (selectedHabit == HabitOption.NUTRITION) {
                            viewModel.calculateAndSaveFoodHabit(
                                foodDescription = primaryInputValue,
                                userId = userId,
                                notes = notesValue,
                                mood = selectedMood,
                                date = selectedDate
                            )
                        } else {
                            val habitType = mapHabitOptionToType(selectedHabit)
                            val record = HabitRecord(
                                userId = userId,
                                type = habitType,
                                value = primaryInputValue.toDoubleOrNull() ?: 0.0,
                                unit = getUnitForHabitType(habitType),
                                notes = notesValue,
                                mood = selectedMood.name,
                                recordDate = Timestamp(selectedDate)
                            )
                            viewModel.saveHabitRecord(record)
                        }
                        onBackClick()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = primaryInputValue.isNotBlank()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodSelector(
    selectedMood: MoodOption,
    onMoodSelected: (MoodOption) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Mood,
                contentDescription = "Estado de ánimo",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Estado de ánimo",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MoodOption.values()) { mood ->
                FilterChip(
                    selected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) },
                    label = { Text("${mood.emoji} ${mood.displayName}") }
                )
            }
        }
    }
}

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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.margin_medium))
    ) {
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            HabitOption.SLEEP -> {
                OutlinedTextField(
                    value = primaryValue,
                    onValueChange = onPrimaryValueChange,
                    label = { Text("Horas de sueño") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            HabitOption.NUTRITION -> {
                OutlinedTextField(
                    value = primaryValue,
                    onValueChange = onPrimaryValueChange,
                    label = { Text("¿Qué comiste? (ej: '1 apple and 1 coffee')") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Campo de notas
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Notes,
                contentDescription = "Notas",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = notesValue,
                onValueChange = onNotesValueChange,
                label = { Text(stringResource(id = R.string.notes_optional)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun RegisterHabitScreenPreview() {
    VivePasoAPasoTheme {
        RegisterHabitScreen()
    }
}
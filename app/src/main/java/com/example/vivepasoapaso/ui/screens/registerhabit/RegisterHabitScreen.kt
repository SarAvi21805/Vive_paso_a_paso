package com.example.vivepasoapaso.ui.screens.registerhabit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.screens.dashboard.BottomNavBar
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterHabitScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Padding para no superponerse con la barra
            contentAlignment = Alignment.Center // Centrando el contenido
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.register_habit_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(id = R.string.date_label),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                HabitScrollableSelector() // Composable para los botones

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                OutlinedTextField(
                    value = "45",
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.minutes_of_exercise)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

                OutlinedTextField(
                    value = "Clase de spinning, alta intensidad",
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.notes_optional)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    Button(onClick = { /* No action */ }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.save_button))
                    }
                    OutlinedButton(onClick = { /* No action */ }, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                }
            }
        }
    }
}

@Composable
fun HabitScrollableSelector() {
    // El estado funciona exactamente igual que antes
    var selectedHabit by remember { mutableStateOf("Ejercicio") }
    val habits = listOf("Agua", "Sueño", "Ejercicio", "Alimentación", "Meditar", "Leer") // Hábitos

    // LazyRow crea una fila horizontal que se puede deslizar
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        items(habits) { habit ->
            val isSelected = habit == selectedHabit

            // La lógica de selección es la misma
            if (isSelected) {
                Button(onClick = { selectedHabit = habit }) {
                    Text(text = habit)
                }
            } else {
                OutlinedButton(onClick = { selectedHabit = habit }) {
                    Text(text = habit)
                }
            }
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
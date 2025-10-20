package com.example.vivepasoapaso.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme

@Composable
fun ProgressScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.your_progress),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Button(onClick = { /* No action */ }, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.weekly))
            }
            OutlinedButton(onClick = { /* No action */ }, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.all_habits))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

        // Gráfico de barras simulado
        BarChart()

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

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
            Card(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.avg_sleep),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
            Card(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.total_steps),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
        }
    }
}

@Composable
fun BarChart() {
    // Esto es solo una representación visual, no un gráfico real.
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
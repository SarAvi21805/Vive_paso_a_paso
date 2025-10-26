package com.example.vivepasoapaso.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import androidx.compose.ui.res.painterResource as painterResource1

@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "App Logo",
            modifier = Modifier.size(width = 100.dp, height = 100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(stringResource(id = R.string.email_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(stringResource(id = R.string.password_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.login_button))
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { /* No action */ }) {
                Text(stringResource(id = R.string.create_account))
            }
            TextButton(onClick = { /* No action */ }) {
                Text(stringResource(id = R.string.forgot_password))
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))
        Text("O inicia sesión con")
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            // Iconos centrados
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón para Google
            OutlinedButton(
                onClick = { /* No action */ },
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            ) {
                Image(
                    painter = painterResource1(id = R.drawable.google_logo),
                    contentDescription = "Iniciar sesión con Google", // Para accesibilidad
                    modifier = Modifier.size(24.dp) // tamaño
                )
            }
            // Botón para Apple
            OutlinedButton(
                onClick = { /* No action */ },
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            ) {
                Image(
                    painter = painterResource1(id = R.drawable.apple_logo),
                    contentDescription = "Iniciar sesión con Apple", // Para accesibilidad
                    modifier = Modifier.size(24.dp) // tamaño
                )
            }
            // Botón para Facebook
            OutlinedButton(
                onClick = { /* No action */ },
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            ) {
                Image(
                    painter = painterResource1(id = R.drawable.facebook_logo),
                    contentDescription = "Iniciar sesión con Facebook", // Para accesibilidad
                    modifier = Modifier.size(24.dp) // tamaño
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    VivePasoAPasoTheme {
        LoginScreen()
    }
}
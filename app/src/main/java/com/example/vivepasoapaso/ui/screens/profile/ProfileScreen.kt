package com.example.vivepasoapaso.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_nav),
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
            //Avatar y nombre
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
            Text(text = "Usuario", style = MaterialTheme.typography.headlineSmall)
            Text(text = "usuario@email.com", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Preferencias del usuario
            SectionTitle(title = stringResource(id = R.string.preferences))
            ProfileOption(
                text = stringResource(id = R.string.notifications),
                hasToggle = true
            )
            ProfileOption(text = stringResource(id = R.string.personalize_goals))

            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)))

            //Cuenta y Seguridad
            SectionTitle(title = stringResource(id = R.string.account_and_security))
            ProfileOption(text = stringResource(id = R.string.change_password))
            ProfileOption(text = stringResource(id = R.string.privacy_policy))

            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)))

            //Botones de comentario, ayuda y cierre de sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                OutlinedButton(onClick = { /* Enviar comentarios */ }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.send_feedback))
                }
                OutlinedButton(onClick = { /* Ayuda y FAQ */ }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.help_and_faq))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            Button(
                onClick = { /* Cierre de sesión */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = stringResource(id = R.string.logout))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.padding_small))
    )
}

@Composable
fun ProfileOption(text: String, hasToggle: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        if (hasToggle) {
            Switch(checked = true, onCheckedChange = {})
        } else {
            Text(text = ">")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    VivePasoAPasoTheme {
        ProfileScreen()
    }
}
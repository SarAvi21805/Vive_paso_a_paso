package com.example.vivepasoapaso.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.example.vivepasoapaso.util.LocaleManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    val currentLanguage = remember {
        mutableStateOf(LocaleManager.getDisplayLanguage(context))
    }

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
                    . background(MaterialTheme.colorScheme.secondary)
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
            ProfileOption(text = stringResource(id = R.string.language))

            //Idioma
            ProfileOption(
                text = stringResource(id = R.string.language),
                value = currentLanguage.value,
                onClick = { showLanguageDialog = true }
            )

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

        //Diálogo de selección de idioma
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { languageCode ->
                    //Cambiar idioma
                    val newContext = LocaleManager.setLocale(context, languageCode)
                    currentLanguage.value = LocaleManager.getDisplayLanguage(newContext)
                    showLanguageDialog = false

                    //Recargar la actividad para aplicar cambios
                    (context as? android.app.Activity)?.recreate()
                },
                currentLanguage = LocaleManager.getCurrentLanguage(context)
            )
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
fun ProfileOption(
    text: String,
    value: String? = null,
    hasToggle: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            .clickable { onClick() }
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        if (hasToggle) {
            Switch(checked = true, onCheckedChange = {})
        } else if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        } else {
            Text(text = ">")
        }
    }
}

//Diálogo de selección de idioma
@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    currentLanguage: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.select_language), style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                LanguageOption(
                    languageCode = "es",
                    languageName = stringResource(R.string.language_spanish),
                    isSelected = currentLanguage == "es",
                    onSelected = onLanguageSelected
                )
                LanguageOption(
                    languageCode = "en",
                    languageName = stringResource(R.string.language_english),
                    isSelected = currentLanguage == "en",
                    onSelected = onLanguageSelected
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun LanguageOption(
    languageCode: String,
    languageName: String,
    isSelected: Boolean,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onSelected(languageCode) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(languageCode) }
        )
        Text(
            text = languageName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    VivePasoAPasoTheme {
        ProfileScreen()
    }
}
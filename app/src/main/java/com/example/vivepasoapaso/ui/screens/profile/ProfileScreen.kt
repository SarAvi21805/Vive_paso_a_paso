package com.example.vivepasoapaso.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.ui.theme.VivePasoAPasoTheme
import com.example.vivepasoapaso.util.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    //Estados temporales - sin ViewModel por ahora
    val snackbarHostState = remember { SnackbarHostState() }
    val isUpdatingGoals = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    //Estados para datos de usuario (temporales)
    val currentLanguage = remember {
        mutableStateOf(LocaleManager.getDisplayLanguage(context))
    }
    val currentLanguageCode = remember {
        mutableStateOf(LocaleManager.getCurrentLanguage(context))
    }
    val userName = remember { mutableStateOf("Usuario") }
    val userEmail = remember { mutableStateOf("usuario@email.com") }

    //Estado para la imagen de perfil
    var profileImage by remember { mutableStateOf(ImageManager.loadProfileImage(context)) }

    //Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            profileImage = ImageManager.loadProfileImage(context)
        }
    }

    //Launcher para la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (ImageManager.saveProfileImageFromUri(context, it)) {
                profileImage = ImageManager.loadProfileImage(context)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            //Avatar con imagen o inicial
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (profileImage != null) {
                    Image(
                        bitmap = profileImage!!.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Text(
                        text = "U",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 48.sp
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Agregar foto",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))
            Text(
                text = userName.value,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = userEmail.value,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Preferencias del usuario
            SectionTitle(title = stringResource(id = R.string.preferences))
            ProfileOption(
                text = stringResource(id = R.string.notifications),
                icon = Icons.Default.Notifications,
                hasToggle = true
            )
            ProfileOption(
                text = stringResource(id = R.string.personalize_goals),
                icon = Icons.Default.FitnessCenter,
                onClick = { showGoalsDialog = true }
            )

            ProfileOption(
                text = stringResource(id = R.string.language),
                icon = Icons.Default.Language,
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
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Función de comentarios próximamente")
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.send_feedback))
                }
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Centro de ayuda próximamente")
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.help_and_faq))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            Button(
                onClick = {
                    ImageManager.deleteProfileImage(context)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Sesión cerrada correctamente")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = stringResource(id = R.string.logout))
            }
        }

        //Diálogo para seleccionar fuente de imagen
        if (showImageSourceDialog) {
            ImageSourceDialog(
                onDismiss = { showImageSourceDialog = false },
                onCameraSelected = {
                    if (PermissionManager.hasCameraPermission(context)) {
                        val file = ImageManager.createImageFile(context)
                        val uri = ImageManager.getImageUri(context, file)
                        if (ImageManager.saveProfileImage(context, ImageManager.uriToBitmap(context, uri)!!)) {
                            cameraLauncher.launch(uri)
                        }
                    } else {
                        //En una implementación real, aquí pedirías permisos
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Permiso de cámara necesario")
                        }
                    }
                },
                onGallerySelected = {
                    if (PermissionManager.hasStoragePermission(context)) {
                        galleryLauncher.launch("image/*")
                    } else {
                        //En una implementación real, aquí pedirías permisos
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Permiso de almacenamiento necesario")
                        }
                    }
                }
            )
        }

        //Diálogo de selección de idioma
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguageCode = currentLanguageCode.value,
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { languageCode ->
                    val newContext = LocaleManager.setLocale(context, languageCode)
                    currentLanguage.value = LocaleManager.getDisplayLanguage(newContext)
                    currentLanguageCode.value = languageCode
                    showLanguageDialog = false
                    //En una implementación real, aquí actualizarías en Firebase
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Idioma cambiado a ${if (languageCode == "es") "Español" else "English"}")
                    }
                }
            )
        }

        //Diálogo para personalizar metas
        if (showGoalsDialog) {
            PersonalizeGoalsDialog(
                onDismiss = { showGoalsDialog = false },
                onGoalsSaved = { goals ->
                    isUpdatingGoals.value = true
                    coroutineScope.launch {
                        //Simular actualización
                        kotlinx.coroutines.delay(1000)
                        isUpdatingGoals.value = false
                        snackbarHostState.showSnackbar("¡Metas actualizadas correctamente!")
                        showGoalsDialog = false
                    }
                },
                currentGoals = null,
                isLoading = isUpdatingGoals.value
            )
        }
    }
}

//Diálogo para seleccionar fuente de imagen
@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar foto de perfil") },
        text = { Text("¿Cómo quieres agregar tu foto de perfil?") },
        confirmButton = {
            Button(onClick = {
                onCameraSelected()
                onDismiss()
            }) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tomar foto")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onGallerySelected()
                onDismiss()
            }) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galería")
            }
        }
    )
}

//Diálogo de selección de idioma
@Composable
fun LanguageSelectionDialog(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
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
                    isSelected = currentLanguageCode == "es",
                    onSelected = onLanguageSelected
                )
                LanguageOption(
                    languageCode = "en",
                    languageName = stringResource(R.string.language_english),
                    isSelected = currentLanguageCode == "en",
                    onSelected = onLanguageSelected
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
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
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
            }
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }

        if (hasToggle) {
            Switch(checked = true, onCheckedChange = {})
        } else if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        } else {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

//Diálogo para personalizar metas (mantener igual)
@Composable
fun PersonalizeGoalsDialog(
    onDismiss: () -> Unit,
    onGoalsSaved: (com.example.vivepasoapaso.data.model.DailyGoals) -> Unit,
    currentGoals: com.example.vivepasoapaso.data.model.DailyGoals?,
    isLoading: Boolean = false
) {
    var water by remember { mutableStateOf(currentGoals?.water?.toString() ?: "2.0") }
    var sleep by remember { mutableStateOf(currentGoals?.sleep?.toString() ?: "8.0") }
    var steps by remember { mutableStateOf(currentGoals?.steps?.toString() ?: "10000") }
    var exercise by remember { mutableStateOf(currentGoals?.exercise?.toString() ?: "30") }
    var calories by remember { mutableStateOf(currentGoals?.calories?.toString() ?: "2000") }

    var waterError by remember { mutableStateOf<String?>(null) }
    var sleepError by remember { mutableStateOf<String?>(null) }
    var stepsError by remember { mutableStateOf<String?>(null) }
    var exerciseError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }

    fun validateFields(): Boolean {
        var isValid = true

        waterError = null
        val waterValue = water.toDoubleOrNull()
        if (waterValue == null || waterValue < 0.5 || waterValue > 10) {
            waterError = "Debe ser entre 0.5 y 10 litros"
            isValid = false
        }

        sleepError = null
        val sleepValue = sleep.toDoubleOrNull()
        if (sleepValue == null || sleepValue < 4 || sleepValue > 16) {
            sleepError = "Debe ser entre 4 y 16 horas"
            isValid = false
        }

        stepsError = null
        val stepsValue = steps.toIntOrNull()
        if (stepsValue == null || stepsValue < 1000 || stepsValue > 50000) {
            stepsError = "Debe ser entre 1,000 y 50,000 pasos"
            isValid = false
        }

        exerciseError = null
        val exerciseValue = exercise.toIntOrNull()
        if (exerciseValue == null || exerciseValue < 5 || exerciseValue > 300) {
            exerciseError = "Debe ser entre 5 y 300 minutos"
            isValid = false
        }

        caloriesError = null
        val caloriesValue = calories.toIntOrNull()
        if (caloriesValue == null || caloriesValue < 500 || caloriesValue > 5000) {
            caloriesError = "Debe ser entre 500 y 5,000 calorías"
            isValid = false
        }

        return isValid
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text("Personalizar Metas Diarias", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Actualizando metas...")
                        }
                    }
                } else {
                    GoalInputField(
                        label = "Agua (litros)",
                        value = water,
                        onValueChange = { water = it },
                        unit = "L",
                        error = waterError
                    )
                    GoalInputField(
                        label = "Sueño (horas)",
                        value = sleep,
                        onValueChange = { sleep = it },
                        unit = "h",
                        error = sleepError
                    )
                    GoalInputField(
                        label = "Pasos",
                        value = steps,
                        onValueChange = { steps = it },
                        unit = "pasos",
                        error = stepsError
                    )
                    GoalInputField(
                        label = "Ejercicio (minutos)",
                        value = exercise,
                        onValueChange = { exercise = it },
                        unit = "min",
                        error = exerciseError
                    )
                    GoalInputField(
                        label = "Calorías",
                        value = calories,
                        onValueChange = { calories = it },
                        unit = "kcal",
                        error = caloriesError
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateFields()) {
                        val goals = com.example.vivepasoapaso.data.model.DailyGoals(
                            water = water.toDouble(),
                            sleep = sleep.toDouble(),
                            steps = steps.toInt(),
                            exercise = exercise.toInt(),
                            calories = calories.toInt()
                        )
                        onGoalsSaved(goals)
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isLoading) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun GoalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    error: String?
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = error != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = when (unit) {
                        "L", "h" -> KeyboardType.Decimal
                        else -> KeyboardType.Number
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(unit, style = MaterialTheme.typography.bodyMedium)
        }
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
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
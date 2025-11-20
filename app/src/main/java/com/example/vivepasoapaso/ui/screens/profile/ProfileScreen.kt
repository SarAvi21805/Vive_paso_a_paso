package com.example.vivepasoapaso.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Info
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
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.vivepasoapaso.presentation.auth.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val auth: FirebaseAuth? = try {
        Firebase.auth
    } catch (e: IllegalStateException) {
        null
    }
    val currentUser = auth?.currentUser

    //Estados
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    //Idioma siempre actualizado
    val currentLanguage = remember {
        mutableStateOf(LocaleManager.getDisplayLanguage(context))
    }
    val currentLanguageCode = remember {
        mutableStateOf(LocaleManager.getCurrentLanguage(context))
    }

    var profileImage by remember { mutableStateOf(ImageManager.loadProfileImage(context)) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            profileImage = ImageManager.loadProfileImage(context)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (ImageManager.saveProfileImageFromUri(context, it)) {
                profileImage = ImageManager.loadProfileImage(context)
            }
        }
    }

    val cameraPermissionLauncher = PermissionManager.rememberCameraPermissionLauncher(
        onPermissionGranted = {
            val file = ImageManager.createImageFile(context)
            val uri = ImageManager.getImageUri(context, file)
            cameraLauncher.launch(uri)
        },
        onPermissionDenied = {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permiso de cámara denegado")
            }
        }
    )

    val storagePermissionLauncher = PermissionManager.rememberStoragePermissionLauncher(
        onPermissionGranted = {
            galleryLauncher.launch("image/*")
        },
        onPermissionDenied = {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permiso de almacenamiento denegado")
            }
        }
    )

    //Actualizar idioma cada vez que la pantalla se muestre
    LaunchedEffect(Unit) {
        currentLanguage.value = LocaleManager.getDisplayLanguage(context)
        currentLanguageCode.value = LocaleManager.getCurrentLanguage(context)

        //Cargar estado de notificaciones
        DataStoreManager.getNotificationsEnabled(context).collect { enabled ->
            notificationsEnabled = enabled
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
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Avatar con ícono de cámara enfrente
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
                } else {
                    Text(
                        text = currentUser?.displayName?.take(1)?.uppercase() ?: "U",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 48.sp
                    )
                }

                // Ícono de cámara enfrente del avatar
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
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_medium)))

            //Información del usuario
            Text(
                text = currentUser?.displayName ?: "Usuario",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = currentUser?.email ?: "usuario@email.com",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Preferencias
            SectionTitle(title = stringResource(id = R.string.preferences))

            //Opción de notificaciones con toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.padding_medium)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        text = stringResource(id = R.string.notifications),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        notificationsEnabled = enabled
                        coroutineScope.launch {
                            DataStoreManager.setNotificationsEnabled(context, enabled)
                            snackbarHostState.showSnackbar(
                                if (enabled) "Notificaciones activadas"
                                else "Notificaciones desactivadas"
                            )
                        }
                    }
                )
            }

            //Personalizar metas
            ProfileOption(
                text = stringResource(id = R.string.personalize_goals),
                icon = Icons.Default.FitnessCenter,
                onClick = { showGoalsDialog = true }
            )

            //Idioma siempre actualizado
            ProfileOption(
                text = stringResource(id = R.string.language),
                icon = Icons.Default.Language,
                value = currentLanguage.value,
                onClick = { showLanguageDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)))

            //Cuenta y Seguridad - Solo mostrar si el usuario está logueado
            if (currentUser != null) {
                SectionTitle(title = stringResource(id = R.string.account_and_security))
                ProfileOption(
                    text = stringResource(id = R.string.change_password),
                    onClick = { showChangePasswordDialog = true }
                )
            }

            ProfileOption(
                text = stringResource(id = R.string.privacy_policy),
                icon = Icons.Default.Info,
                onClick = { showPrivacyPolicyDialog = true }
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

            //Botón de cerrar sesión o iniciar sesión
            if (currentUser != null) {
                Button(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(id = R.string.logout))
                }
            } else {
                Button(
                    onClick = { onNavigateToLogin() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = stringResource(id = R.string.login_button))
                }
            }
        }

        if (showGoalsDialog) {
            SimpleGoalsDialog(
                onDismiss = { showGoalsDialog = false },
                onGoalsSaved = { goals ->
                    authViewModel.updateDailyGoals(goals)
                    showGoalsDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Metas actualizadas correctamente")
                    }
                }
            )
        }

        //Diálogo para seleccionar fuente de imagen
        if (showImageSourceDialog) {
            ImageSourceDialog(
                onDismiss = { showImageSourceDialog = false },
                onCameraSelected = {
                    showImageSourceDialog = false
                    if (PermissionManager.hasCameraPermission(context)) {
                        val file = ImageManager.createImageFile(context)
                        val uri = ImageManager.getImageUri(context, file)
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },
                onGallerySelected = {
                    showImageSourceDialog = false
                    if (PermissionManager.hasStoragePermission(context)) {
                        galleryLauncher.launch("image/*")
                    } else {
                        storagePermissionLauncher.launch(PermissionManager.getStoragePermissions())
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

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            if (languageCode == "es") "Idioma cambiado a Español"
                            else "Language changed to English"
                        )
                    }

                    //Forzar actualización de la actividad
                    (context as? android.app.Activity)?.recreate()
                }
            )
        }

        //Diálogo para cambiar contraseña - Solo si el usuario está logueado
        if (showChangePasswordDialog && currentUser != null) {
            ChangePasswordDialog(
                onDismiss = { showChangePasswordDialog = false },
                onPasswordChanged = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Contraseña cambiada correctamente")
                    }
                }
            )
        }

        if (showPrivacyPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyPolicyDialog = false },
                title = {
                    Text(
                        text = stringResource(id = R.string.privacy_policy),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.privacy_policy_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = if (LocaleManager.getCurrentLanguage(context) == "es") "Cerrar sesión" else "Logout"
                    )
                },
                text = {
                    Text(
                        text = if (LocaleManager.getCurrentLanguage(context) == "es")
                            "¿Estás seguro de que quieres cerrar sesión?"
                        else
                            "Are you sure you want to logout?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            auth?.signOut()
                            onNavigateToLogin()
                        }
                    ) {
                        Text(if (LocaleManager.getCurrentLanguage(context) == "es") "Sí, cerrar sesión" else "Yes, logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(if (LocaleManager.getCurrentLanguage(context) == "es") "Cancelar" else "Cancel")
                    }
                }
            )
        }
    }
}

//Diálogo simplificado de metas
@Composable
fun SimpleGoalsDialog(
    onDismiss: () -> Unit,
    onGoalsSaved: (com.example.vivepasoapaso.data.model.DailyGoals) -> Unit
) {
    var water by remember { mutableStateOf("2.0") }
    var sleep by remember { mutableStateOf("8.0") }
    var steps by remember { mutableStateOf("10000") }
    var exercise by remember { mutableStateOf("30") }
    var calories by remember { mutableStateOf("2000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Personalizar Metas Diarias", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                GoalInputField(
                    label = "Agua (litros)",
                    value = water,
                    onValueChange = { water = it },
                    unit = "L"
                )
                GoalInputField(
                    label = "Sueño (horas)",
                    value = sleep,
                    onValueChange = { sleep = it },
                    unit = "h"
                )
                GoalInputField(
                    label = "Pasos",
                    value = steps,
                    onValueChange = { steps = it },
                    unit = "pasos"
                )
                GoalInputField(
                    label = "Ejercicio (minutos)",
                    value = exercise,
                    onValueChange = { exercise = it },
                    unit = "min"
                )
                GoalInputField(
                    label = "Calorías",
                    value = calories,
                    onValueChange = { calories = it },
                    unit = "kcal"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goals = com.example.vivepasoapaso.data.model.DailyGoals(
                        water = water.toDoubleOrNull() ?: 2.0,
                        sleep = sleep.toDoubleOrNull() ?: 8.0,
                        steps = steps.toIntOrNull() ?: 10000,
                        exercise = exercise.toIntOrNull() ?: 30,
                        calories = calories.toIntOrNull() ?: 2000
                    )
                    onGoalsSaved(goals)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

//Diálogo para cambiar contraseña
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth: FirebaseAuth? = try {
        Firebase.auth
    } catch (e: IllegalStateException) {
        null
    }
    val user = auth?.currentUser

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Cambiar Contraseña") },
        text = {
            Column {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        errorMessage = null
                    },
                    label = { Text("Contraseña actual") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("Nueva contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirmar nueva contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (user == null) {
                        errorMessage = "No hay usuario autenticado"
                        return@Button
                    }

                    if (newPassword != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@Button
                    }

                    if (newPassword.length < 6) {
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        return@Button
                    }

                    isLoading = true

                    //Reautenticar y cambiar contraseña
                    val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)
                    user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                isLoading = false
                                if (updateTask.isSuccessful) {
                                    onPasswordChanged()
                                    onDismiss()
                                } else {
                                    errorMessage = "Error al cambiar contraseña: ${updateTask.exception?.message}"
                                }
                            }
                        } else {
                            isLoading = false
                            errorMessage = "Contraseña actual incorrecta"
                        }
                    }
                },
                enabled = !isLoading && currentPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isLoading) "Cambiando..." else "Cambiar")
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
            }) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tomar foto")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onGallerySelected()
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
    icon: ImageVector? = null,
    value: String? = null,
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

        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        } else {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun GoalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
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
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    VivePasoAPasoTheme {
        ProfileScreen()
    }
}
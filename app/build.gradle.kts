import java.util.Properties
import java.io.FileInputStream

// Carga de local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.hilt.android) // Hilt Plugin
    alias(libs.plugins.ksp) // KSP para el compilador de Hilt
}

android {
    namespace = "com.example.vivepasoapaso"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vivepasoapaso"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
            buildConfigField("String", "OPENAI_API_KEY", "\"${properties.getProperty("OPENAI_API_KEY", "")}\"")
        } else {
            buildConfigField("String", "OPENAI_API_KEY", "\"\"")
        }

        buildConfigField("String", "EDAMAM_APP_ID", "\"${localProperties.getProperty("EDAMAM_APP_ID")}\"")
        buildConfigField("String", "EDAMAM_APP_KEY", "\"${localProperties.getProperty("EDAMAM_APP_KEY")}\"")
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"${localProperties.getProperty("OPENWEATHER_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // KOTLIN - ¡ESTA LÍNEA ES CLAVE! Arregla el error de versión incompatible.
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.0"))

    // ANDROIDX CORE & COMPOSE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // El BOM ya incluye ui, graphics, etc.
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // HILT (Inyección de Dependencias)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ROOM (Base de Datos Local) - ¡ESTO FALTABA! Arregla el error NonExistentClass.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Para soporte de Coroutines y Flow
    ksp(libs.androidx.room.compiler)

    // RETROFIT & MOSHI (Para las APIs)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // COROUTINES (Sin duplicados)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // TESTS
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
<<<<<<< Updated upstream
    id("com.google.dagger.hilt.android") version "2.48" apply false
    //id("com.android.application") version "8.2.0" apply false
    //id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    alias(libs.plugins.ksp) apply false
=======
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

// Versiones específicas para evitar conflictos
buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
>>>>>>> Stashed changes
}
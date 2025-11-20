package com.example.vivepasoapaso

import android.app.Application
import android.content.Context
import com.example.vivepasoapaso.service.NotificationService
import com.example.vivepasoapaso.util.LocaleManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VivePasoAPasoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: IllegalStateException) {
        }

        // Inicializar notificaciones
        NotificationService.createNotificationChannel(this)
        NotificationService.scheduleDailyReminder(this)

        instance = this
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base, LocaleManager.getCurrentLanguage(base)))
    }

    companion object {
        lateinit var instance: VivePasoAPasoApplication
            private set
    }
}
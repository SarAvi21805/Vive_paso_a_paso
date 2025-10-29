package com.example.vivepasoapaso

import android.app.Application
import android.content.Context
import com.example.vivepasoapaso.util.LocaleManager
import com.google.firebase.FirebaseApp

class VivePasoAPasoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Inicializar Firebase explícitamente
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: IllegalStateException) {
            //Firebase ya estaba inicializado, ignorar
        }

        instance = this
    }

    override fun attachBaseContext(base: Context) {
        //Aplicar el locale guardado antes de que se creen las vistas
        super.attachBaseContext(LocaleManager.setLocale(base, LocaleManager.getCurrentLanguage(base)))
    }

    companion object {
        lateinit var instance: VivePasoAPasoApplication
            private set
    }
}
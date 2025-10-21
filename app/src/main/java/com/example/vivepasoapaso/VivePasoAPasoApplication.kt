package com.example.vivepasoapaso

import android.app.Application
import android.content.Context
import com.example.vivepasoapaso.util.LocaleManager

class VivePasoAPasoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Inicializaciones globales de la app
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
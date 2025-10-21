package com.example.vivepasoapaso.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.example.vivepasoapaso.R
import java.util.*

object LocaleManager {
    private const val PREF_LANGUAGE = "pref_language"
    private const val DEFAULT_LANGUAGE = "es"

    fun setLocale(context: Context, language: String): Context {
        persistLanguage(context, language)
        return updateResources(context, language)
    }

    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return prefs.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    private fun persistLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_LANGUAGE, language).apply()
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            context
        }
    }

    fun getDisplayLanguage(context: Context): String {
        return when (getCurrentLanguage(context)) {
            "es" -> context.getString(R.string.language_spanish)
            "en" -> context.getString(R.string.language_english)
            else -> context.getString(R.string.language_spanish)
        }
    }
}
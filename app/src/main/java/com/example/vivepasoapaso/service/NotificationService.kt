package com.example.vivepasoapaso.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.vivepasoapaso.MainActivity
import com.example.vivepasoapaso.R
import com.example.vivepasoapaso.util.LocaleManager
import java.util.*

object NotificationService {
    private const val CHANNEL_ID = "habit_reminder_channel"
    private const val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showHabitReminder(context: Context) {
        val currentLanguage = LocaleManager.getCurrentLanguage(context)
        val (title, message) = if (currentLanguage == "es") {
            "¡Recordatorio de hábitos!" to "No olvides registrar tus hábitos de hoy"
        } else {
            "Habit Reminder!" to "Don't forget to log your habits for today"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun scheduleDailyReminder(context: Context) {
        // Programar notificación diaria a las 8 PM
        android.os.Handler().postDelayed({
            showHabitReminder(context)
        }, 1000 * 60 * 60 * 12) // 12 horas después
    }
}
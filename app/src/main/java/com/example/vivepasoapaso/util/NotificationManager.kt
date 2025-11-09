package com.example.vivepasoapaso.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vivepasoapaso.R
import java.util.*

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        showDailyReminder()
        return Result.success()
    }

    private fun showDailyReminder() {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        // Verificar permiso para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, "habit_channel")
            .setContentTitle(applicationContext.getString(R.string.daily_reminder_title))
            .setContentText(applicationContext.getString(R.string.daily_reminder_content))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(getDailyNotificationId(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habit_channel",
                "Recordatorios de Hábitos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios para registrar tus hábitos diarios"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getDailyNotificationId(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }
}
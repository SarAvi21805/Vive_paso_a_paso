package com.example.vivepasoapaso.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",

    val avatar: String = generateDefaultAvatar(name),

    @PropertyName("created_at")
    val createdAt: Timestamp = Timestamp.now(),

    @PropertyName("updated_at")
    val updatedAt: Timestamp = Timestamp.now(),

    //Preferencias del usuario
    val language: String = "es",

    @PropertyName("daily_goals")
    val dailyGoals: DailyGoals = DailyGoals(),

    @PropertyName("notification_enabled")
    val notificationEnabled: Boolean = true
)

data class DailyGoals(
    val water: Double = 2.0, //litros
    val sleep: Double = 8.0, //horas
    val steps: Int = 10000, //pasos
    val exercise: Int = 30, //minutos
    val calories: Int = 2000 //calorías
)

//Función para generar avatar por defecto basado en el nombre
fun generateDefaultAvatar(name: String): String {
    return if (name.isNotEmpty()) {
        name.first().uppercase()
    } else {
        "U"
    }
}
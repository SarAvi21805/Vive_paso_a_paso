package com.example.vivepasoapaso.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val language: String = "es",
    val dailyGoals: DailyGoals? = null,
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val updatedAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)

data class DailyGoals(
    val water: Double = 2.0, // litros
    val sleep: Double = 8.0, // horas
    val steps: Int = 10000, // pasos
    val exercise: Int = 30, // minutos
    val calories: Int = 2000 // calorías
)
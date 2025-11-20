package com.example.vivepasoapaso.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class HabitRecord(
    val id: String = "",

    @PropertyName("user_id")
    val userId: String = "",

    val type: HabitType = HabitType.EXERCISE,
    val value: Double = 0.0,

    @PropertyName("unit")
    val unit: String = "",

    val notes: String? = null,

    val mood: String? = null,

    @PropertyName("record_date")
    val recordDate: Timestamp = Timestamp.now(),

    @PropertyName("created_at")
    val createdAt: Timestamp = Timestamp.now()
)

enum class HabitType {
    WATER,       //Hidratación (litros)
    SLEEP,       //Sueño (horas)
    EXERCISE,    //Ejercicio (minutos)
    STEPS,       //Pasos (número)
    NUTRITION,   //Nutrición (calorías)
    MEDITATION,  //Meditación (minutos)
    READING      //Lectura (minutos)
}
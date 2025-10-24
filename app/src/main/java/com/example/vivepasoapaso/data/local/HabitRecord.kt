// Estructura de las filas de la BD

package com.example.vivepasoapaso.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "habit_records")
data class HabitRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String, // Para identificar al usuario (Firebase)
    val habitType: String, // Hábito (Ej. "Alimentación")
    val amount: Double, // Para cantidades como litros
    val notes: String?,
    val date: Long // Fecha almacenada como un timestamp
)
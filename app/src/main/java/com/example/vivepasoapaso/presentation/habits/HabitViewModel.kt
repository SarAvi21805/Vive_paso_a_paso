package com.example.vivepasoapaso.presentation.habits

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.model.MoodOption
import com.example.vivepasoapaso.data.repository.HabitRepository
import com.example.vivepasoapaso.util.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import com.google.firebase.Timestamp

class HabitViewModel @Inject constructor(private val habitRepository: HabitRepository) : ViewModel() {

    private val _habitState = MutableStateFlow<HabitState>(HabitState.Idle)
    val habitState: StateFlow<HabitState> = _habitState.asStateFlow()

    private val _todayRecords = MutableStateFlow<List<HabitRecord>>(emptyList())
    val todayRecords: StateFlow<List<HabitRecord>> = _todayRecords.asStateFlow()

    private val _weeklyStats = MutableStateFlow<Map<HabitType, Double>>(emptyMap())
    val weeklyStats: StateFlow<Map<HabitType, Double>> = _weeklyStats.asStateFlow()

    fun saveHabitRecord(record: HabitRecord) {
        _habitState.value = HabitState.Loading
        viewModelScope.launch {
            val result = habitRepository.saveHabitRecord(record)
            _habitState.value = when {
                result.isSuccess -> HabitState.Success
                else -> HabitState.Error(result.exceptionOrNull()?.message ?: "Error al guardar")
            }

            //Actualizar registros del día
            record.userId?.let { loadTodayRecords(it) }
        }
    }

    fun loadTodayRecords(userId: String) {
        viewModelScope.launch {
            _todayRecords.value = habitRepository.getTodayHabitRecords(userId)
        }
    }

    fun loadWeeklyStats(userId: String) {
        viewModelScope.launch {
            _weeklyStats.value = habitRepository.getWeeklyStats(userId)
        }
    }

    fun getHabitRecordsByType(userId: String, type: HabitType, limit: Int = 30) {
        viewModelScope.launch {
            val records = habitRepository.getHabitRecordsByType(userId, type, limit)
            _habitState.value = HabitState.RecordsLoaded(records)
        }
    }

    fun clearHabitState() {
        _habitState.value = HabitState.Idle
    }

    fun testApi(food: String) {
        viewModelScope.launch {
            val calories = habitRepository.getCaloriesForFood(food)
            if (calories != null) {
                Log.d("HabitViewModel", "Las calorías de '$food' son: $calories")
            } else {
                Log.e("HabitViewModel", "Falló la llamada a la API para '$food'")
            }
        }
    }

    fun calculateAndSaveFoodHabit(foodDescription: String, userId: String) {
        _habitState.value = HabitState.Loading
        viewModelScope.launch {
            // Llamada al repositorio para obtener las calorías desde la API
            val calories = habitRepository.getCaloriesForFood(foodDescription)

            if (calories != null && calories > 0) {
                // Creación del objeto HabitRecord
                val record = HabitRecord(
                    userId = userId,
                    type = HabitType.NUTRITION,
                    value = calories,
                    recordDate = Timestamp.now() // fecha actual
                )

                // Llamada a la función para guardar
                val result = habitRepository.saveHabitRecord(record)
                _habitState.value = when {
                    result.isSuccess -> HabitState.Success
                    else -> HabitState.Error(result.exceptionOrNull()?.message ?: "Error al guardar")
                }
            } else {
                // API falla o no encontró calorías
                _habitState.value = HabitState.Error("No se pudieron calcular las calorías para '$foodDescription'")
            }

            // Actualizar registros del día
            loadTodayRecords(userId)
        }
    }

    // Nueva función para guardar con estado de ánimo y fecha personalizada
    fun calculateAndSaveFoodHabit(
        foodDescription: String,
        userId: String,
        notes: String,
        mood: MoodOption,
        date: Date
    ) {
        _habitState.value = HabitState.Loading
        viewModelScope.launch {
            val calories = habitRepository.getCaloriesForFood(foodDescription)

            if (calories != null && calories > 0) {
                val record = HabitRecord(
                    userId = userId,
                    type = HabitType.NUTRITION,
                    value = calories,
                    notes = notes,
                    mood = mood.name,
                    recordDate = Timestamp(date)
                )
                val result = habitRepository.saveHabitRecord(record)
                _habitState.value = when {
                    result.isSuccess -> HabitState.Success
                    else -> HabitState.Error(result.exceptionOrNull()?.message ?: "Error al guardar")
                }
            } else {
                _habitState.value = HabitState.Error("No se pudieron calcular las calorías para '$foodDescription'")
            }
            loadTodayRecords(userId)
        }
    }

    // Función para cargar consejo del clima
    suspend fun loadWeatherTip(context: Context): String {
        return try {
            val currentLanguage = LocaleManager.getCurrentLanguage(context)
            // Usar ubicación por defecto (podría mejorarse con GPS)
            val weather = habitRepository.getCurrentWeather(40.7128, -74.0060) // NYC por defecto

            val weatherMain = weather?.weather?.firstOrNull()?.main ?: "Clear"
            val tip = when (weatherMain) {
                "Rain" -> if (currentLanguage == "es") "Llueve hoy - perfecto para actividades indoor" else "Raining today - perfect for indoor activities"
                "Clear" -> if (currentLanguage == "es") "¡Día soleado! Ideal para caminar al aire libre" else "Sunny day! Ideal for outdoor walking"
                "Clouds" -> if (currentLanguage == "es") "Día nublado - buen momento para ejercicio moderado" else "Cloudy day - good time for moderate exercise"
                "Snow" -> if (currentLanguage == "es") "¡Nieva! Cuida tu hidratación en interiores" else "Snowing! Stay hydrated indoors"
                else -> if (currentLanguage == "es") "Mantente activo y hidratado hoy" else "Stay active and hydrated today"
            }
            tip
        } catch (e: Exception) {
            if (LocaleManager.getCurrentLanguage(context) == "es") {
                "Mantén una rutina constante para mejores resultados"
            } else {
                "Keep a consistent routine for better results"
            }
        }
    }
}

sealed class HabitState {
    object Idle : HabitState()
    object Loading : HabitState()
    object Success : HabitState()
    data class Error(val message: String) : HabitState()
    data class RecordsLoaded(val records: List<HabitRecord>) : HabitState()
}
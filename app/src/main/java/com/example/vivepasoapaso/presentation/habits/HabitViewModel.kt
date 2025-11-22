package com.example.vivepasoapaso.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.model.MoodOption
import com.example.vivepasoapaso.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _habitRecords = MutableStateFlow<List<HabitRecord>>(emptyList())
    val habitRecords: StateFlow<List<HabitRecord>> = _habitRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Cargar registros de hábitos
    fun loadHabitRecords(userId: String, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val records = habitRepository.getHabitRecords(userId, startDate, endDate)
                _habitRecords.value = records
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los registros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Cargar registros de hoy
    fun loadTodayHabitRecords(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val records = habitRepository.getTodayHabitRecords(userId)
                _habitRecords.value = records
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los registros de hoy: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Guardar un registro de hábito
    fun saveHabitRecord(habitRecord: HabitRecord) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = habitRepository.saveHabitRecord(habitRecord)
                if (result.isSuccess) {
                    // Actualizar la lista local
                    val currentRecords = _habitRecords.value.toMutableList()
                    currentRecords.add(result.getOrNull() ?: habitRecord)
                    _habitRecords.value = currentRecords
                } else {
                    _errorMessage.value = "Error al guardar el registro"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Calcular y guardar hábito de comida
    fun calculateAndSaveFoodHabit(
        foodDescription: String,
        userId: String,
        notes: String,
        mood: MoodOption,
        date: Date
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Calcular calorías usando Edamam API
                val calories = habitRepository.getCaloriesForFood(foodDescription) ?: 0.0

                val record = HabitRecord(
                    userId = userId,
                    type = HabitType.NUTRITION,
                    value = calories,
                    unit = "cal",
                    notes = if (notes.isNotBlank()) {
                        "Comida: $foodDescription. Notas: $notes"
                    } else {
                        "Comida: $foodDescription"
                    },
                    mood = mood.name,
                    recordDate = com.google.firebase.Timestamp(date)
                )

                val result = habitRepository.saveHabitRecord(record)
                if (result.isSuccess) {
                    // Actualizar la lista local
                    val currentRecords = _habitRecords.value.toMutableList()
                    currentRecords.add(result.getOrNull() ?: record)
                    _habitRecords.value = currentRecords
                } else {
                    _errorMessage.value = "Error al guardar el registro de comida"
                }
            } catch (e: Exception) {
                // En caso de error, guardar sin calorías calculadas
                try {
                    val fallbackRecord = HabitRecord(
                        userId = userId,
                        type = HabitType.NUTRITION,
                        value = 0.0,
                        unit = "cal",
                        notes = if (notes.isNotBlank()) {
                            "Comida: $foodDescription. Notas: $notes"
                        } else {
                            "Comida: $foodDescription"
                        },
                        mood = mood.name,
                        recordDate = com.google.firebase.Timestamp(date)
                    )
                    val result = habitRepository.saveHabitRecord(fallbackRecord)
                    if (result.isSuccess) {
                        val currentRecords = _habitRecords.value.toMutableList()
                        currentRecords.add(result.getOrNull() ?: fallbackRecord)
                        _habitRecords.value = currentRecords
                    }
                } catch (fallbackError: Exception) {
                    _errorMessage.value = "Error al guardar el registro: ${fallbackError.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Eliminar registro de hábito
    fun deleteHabitRecord(recordId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = habitRepository.deleteHabitRecord(recordId)
                if (result.isSuccess) {
                    // Remover de la lista local
                    val currentRecords = _habitRecords.value.toMutableList()
                    currentRecords.removeAll { it.id == recordId }
                    _habitRecords.value = currentRecords
                } else {
                    _errorMessage.value = "Error al eliminar el registro"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Obtener estadísticas semanales
    fun loadWeeklyStats(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val stats = habitRepository.getWeeklyStats(userId)
                // Aquí puedes manejar las estadísticas como necesites
                // Por ejemplo, emitirlas en otro StateFlow
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar estadísticas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Obtener resumen semanal
    fun loadWeeklySummary(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val summary = habitRepository.getWeeklySummary(userId)
                // Aquí puedes manejar el resumen como necesites
                // Por ejemplo, emitirlo en otro StateFlow
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar resumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Cargar registros por tipo
    fun loadHabitRecordsByType(userId: String, type: HabitType, limit: Int = 30) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val records = habitRepository.getHabitRecordsByType(userId, type, limit)
                _habitRecords.value = records
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar registros por tipo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpiar mensajes de error
    fun clearError() {
        _errorMessage.value = null
    }

    // Limpiar todos los datos
    fun clearData() {
        _habitRecords.value = emptyList()
        _errorMessage.value = null
    }
}
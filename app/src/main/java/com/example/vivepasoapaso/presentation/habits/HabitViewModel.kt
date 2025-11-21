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
import com.google.firebase.Timestamp

<<<<<<< Updated upstream
class HabitViewModel @Inject constructor(private val habitRepository: HabitRepository) : ViewModel() {
=======
@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {
>>>>>>> Stashed changes

    private val _state = MutableStateFlow(HabitState())
    val state: StateFlow<HabitState> = _state.asStateFlow()

    // Hacer estos métodos públicos
    fun saveHabitRecord(record: HabitRecord) {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = habitRepository.saveHabitRecord(record)
            _state.value = when {
                result.isSuccess -> {
                    // Disparar actualización
                    HabitState(
                        isLoading = false,
                        isSuccess = true,
                        refreshTrigger = _state.value.refreshTrigger + 1
                    )
                }
                else -> {
                    HabitState(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al guardar el hábito"
                    )
                }
            }

            // Actualizar registros del día
            record.userId?.let { loadTodayHabits(it) }
        }
    }

    fun calculateAndSaveFoodHabit(
        foodDescription: String,
        userId: String,
        notes: String,
        mood: MoodOption,
        date: Date
    ) {
        _state.value = _state.value.copy(isLoading = true, error = null)

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
                saveHabitRecord(record)
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No se pudieron calcular las calorías para '$foodDescription'"
                )
            }
        }
    }

    fun loadTodayHabits(userId: String) {
        viewModelScope.launch {
            try {
                val todayHabits = habitRepository.getTodayHabitRecords(userId)
                _state.value = _state.value.copy(todayHabits = todayHabits)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al cargar hábitos del día: ${e.message}"
                )
            }
        }
    }

    fun loadWeeklyStats(userId: String) {
        viewModelScope.launch {
            try {
                val weeklyStats = habitRepository.getWeeklyStats(userId)
                _state.value = _state.value.copy(weeklyStats = weeklyStats)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al cargar estadísticas semanales: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun resetState() {
        _state.value = HabitState()
    }
}

data class HabitState(
    val todayHabits: List<HabitRecord> = emptyList(),
    val weeklyStats: Map<HabitType, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val refreshTrigger: Int = 0
)
package com.example.vivepasoapaso.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun loadUserData(userId: String) {
        loadTodayHabits(userId)
        loadWeatherTip()
    }

    fun loadTodayHabits(userId: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val todayHabits = habitRepository.getTodayHabitRecords(userId)
                _state.value = _state.value.copy(
                    todayHabits = todayHabits,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error al cargar hábitos: ${e.message}"
                )
            }
        }
    }

    private fun loadWeatherTip() {
        viewModelScope.launch {
            try {
                // Para demo, usamos NYC. En producción usar ubicación real
                val weather = habitRepository.getCurrentWeather(40.7128, -74.0060)
                val weatherMain = weather?.weather?.firstOrNull()?.main ?: "Clear"

                val tip = when (weatherMain) {
                    "Rain" -> "Llueve hoy - perfecto para actividades indoor"
                    "Clear" -> "¡Día soleado! Ideal para caminar al aire libre"
                    "Clouds" -> "Día nublado - buen momento para ejercicio moderado"
                    "Snow" -> "¡Nieva! Cuida tu hidratación en interiores"
                    else -> "Mantente activo y hidratado hoy"
                }

                _state.value = _state.value.copy(weatherTip = tip)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    weatherTip = "Mantén una rutina constante para mejores resultados"
                )
            }
        }
    }

    fun refreshData(userId: String) {
        _state.value = _state.value.copy(refreshTrigger = _state.value.refreshTrigger + 1)
        loadTodayHabits(userId)
    }
}

data class DashboardState(
    val todayHabits: List<HabitRecord> = emptyList(),
    val weatherTip: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val refreshTrigger: Int = 0
)
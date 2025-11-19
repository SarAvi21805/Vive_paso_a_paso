package com.example.vivepasoapaso.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.model.HabitRecord
import com.example.vivepasoapaso.data.model.HabitType
import com.example.vivepasoapaso.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel // Hilt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date

@HiltViewModel
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
}

sealed class HabitState {
    object Idle : HabitState()
    object Loading : HabitState()
    object Success : HabitState()
    data class Error(val message: String) : HabitState()
    data class RecordsLoaded(val records: List<HabitRecord>) : HabitState()
}
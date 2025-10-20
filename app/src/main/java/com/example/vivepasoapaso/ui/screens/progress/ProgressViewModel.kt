package com.example.vivepasoapaso.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivepasoapaso.data.repository.ChatRepositoryImpl
import com.example.vivepasoapaso.domain.usecase.GetChatResponseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val getChatResponseUseCase = GetChatResponseUseCase(ChatRepositoryImpl())

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    fun processIntent(intent: ProgressIntent) {
        when (intent) {
            is ProgressIntent.RequestRecommendation -> {
                getAIRecommendation()
            }
            is ProgressIntent.UpdateUserStats -> {
                _state.value = _state.value.copy(
                    sleepHours = intent.sleepHours,
                    steps = intent.steps,
                    waterLiters = intent.waterLiters,
                    exerciseMinutes = intent.exerciseMinutes
                )
                getAIRecommendation()
            }
        }
    }

    private fun getAIRecommendation() {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val userStats = buildUserStatsPrompt()
                val recommendation = getChatResponseUseCase(userStats)
                _state.value = _state.value.copy(
                    aiRecommendation = recommendation.text,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error al obtener recomendación: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun buildUserStatsPrompt(): String {
        val state = _state.value
        return """
            Usuario con los siguientes hábitos semanales:
            - Sueño: ${state.sleepHours} horas promedio
            - Pasos: ${state.steps} pasos diarios
            - Agua: ${state.waterLiters} litros diarios
            - Ejercicio: ${state.exerciseMinutes} minutos diarios
            
            Proporciona una recomendación personalizada breve (máximo 2 oraciones) para mejorar su bienestar. 
            Sé específico y sugiere un hábito pequeño y alcanzable.
        """.trimIndent()
    }
}
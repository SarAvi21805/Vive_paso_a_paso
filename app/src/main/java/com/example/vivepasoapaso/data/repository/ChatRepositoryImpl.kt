package com.example.vivepasoapaso.data.repository

import android.util.Log
import com.example.vivepasoapaso.BuildConfig
import com.example.vivepasoapaso.data.remote.ChatRequest
import com.example.vivepasoapaso.data.remote.MessageDto
import com.example.vivepasoapaso.data.remote.RetrofitClient
import com.example.vivepasoapaso.domain.model.Message

class ChatRepositoryImpl : ChatRepository {
    private val apiService = RetrofitClient.instance

    override suspend fun getAIResponse(userMessage: String): Message {
        Log.d("Health_AI", "Solicitando recomendación para: $userMessage")

        val apiKey = BuildConfig.OPENAI_API_KEY
        if (apiKey.isBlank() || apiKey == "null") {
            Log.e("Health_AI", "API Key no configurada")
            return Message(
                text = "Error: API Key no configurada. Configura tu API key de OpenAI en local.properties",
                isFromUser = false
            )
        }

        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                MessageDto(
                    role = "system",
                    content = "Eres un asistente de salud y bienestar especializado en hábitos saludables. Proporciona recomendaciones breves, prácticas y motivadoras basadas en datos de sueño, ejercicio, hidratación y nutrición. Sé positivo y alentador."
                ),
                MessageDto(role = "user", content = userMessage)
            )
        )

        return try {
            val response = apiService.getChatCompletions(request)
            val aiMessage = response.choices.firstOrNull()?.message?.content
                ?: "No pude generar una recomendación en este momento."

            Message(text = aiMessage, isFromUser = false)
        } catch (e: Exception) {
            Log.e("Health_AI", "Error: ${e.message}", e)
            Message(
                text = "Error de conexión. Verifica tu internet e intenta nuevamente.",
                isFromUser = false
            )
        }
    }
}
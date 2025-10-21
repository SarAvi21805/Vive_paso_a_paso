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
                text = "Configura tu API key de OpenAI en local.properties",
                isFromUser = false
            )
        }

        Log.d("Health_AI", "API Key cargada correctamente")

        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                MessageDto(
                    role = "system",
                    content = "Eres un asistente de salud y bienestar especializado en hábitos saludables. Proporciona recomendaciones breves, prácticas y motivadoras basadas en datos de sueño, ejercicio, hidratación y nutrición. Sé positivo y alentador. Responde siempre en español."
                ),
                MessageDto(role = "user", content = userMessage)
            )
        )

        return try {
            Log.d("Health_AI", "Realizando llamada a la API...")
            val response = apiService.getChatCompletions(request)
            Log.d("Health_AI", "Respuesta recibida exitosamente")

            val aiMessageContent = response.choices.firstOrNull()?.message?.content
                ?: "No pude generar una recomendación en este momento. Intenta nuevamente."

            Message(text = aiMessageContent, isFromUser = false)
        } catch (e: Exception) {
            Log.e("Health_AI", "Error en la llamada a la API: ${e.message}", e)
            Message(
                text = "Error de conexión. Verifica tu internet e intenta nuevamente.",
                isFromUser = false
            )
        }
    }
}
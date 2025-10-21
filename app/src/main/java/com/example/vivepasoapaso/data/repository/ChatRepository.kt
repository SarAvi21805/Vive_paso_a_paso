package com.example.vivepasoapaso.data.repository

import com.example.vivepasoapaso.domain.model.Message

interface ChatRepository {
    suspend fun getAIResponse(userMessage: String): Message
}
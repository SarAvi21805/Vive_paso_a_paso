package com.example.vivepasoapaso.domain.usecase

import com.example.vivepasoapaso.data.repository.ChatRepository
import com.example.vivepasoapaso.domain.model.Message

class GetChatResponseUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userMessage: String): Message {
        return chatRepository.getAIResponse(userMessage)
    }
}
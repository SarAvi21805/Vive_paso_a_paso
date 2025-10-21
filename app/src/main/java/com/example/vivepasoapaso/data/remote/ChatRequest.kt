package com.example.vivepasoapaso.data.remote

data class ChatRequest(
    val model: String,
    val messages: List<MessageDto>
)

data class MessageDto(
    val role: String,
    val content: String
)
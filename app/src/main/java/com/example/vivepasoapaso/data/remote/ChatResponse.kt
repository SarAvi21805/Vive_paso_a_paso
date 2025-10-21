package com.example.vivepasoapaso.data.remote

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageDto
)
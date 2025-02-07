package com.example.aplikasikotlin

data class ChatMessage(
    val fullname: String = "",
    val message: String = "",
    val timestamp: Long = 0
)

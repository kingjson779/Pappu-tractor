package com.example.data.model

data class AdminAccount(
    val id: String,
    val name: String,
    val roleHindi: String,
    val phone: String,
    val pin: String // In-memory prototype PIN; server-side auth notice is provided in UI
)

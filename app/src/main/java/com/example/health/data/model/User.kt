package com.example.health.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val language: String = "en",
    val emergencyContacts: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

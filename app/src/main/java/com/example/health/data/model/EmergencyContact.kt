package com.example.health.data.model

data class EmergencyContact(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val relationship: String, // Family, Friend, Office
    val bloodGroup: String
)

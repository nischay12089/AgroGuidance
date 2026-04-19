package com.nxtbitz.agroguidance.data

data class User(
    val id: String = java.util.UUID.randomUUID().toString(),
    val email: String = "",
    val password: String = "" // In a real app, always hash passwords!
)

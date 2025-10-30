package com.example.proyfronted.backend.Auth.Model

data class RegisterRequest(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)
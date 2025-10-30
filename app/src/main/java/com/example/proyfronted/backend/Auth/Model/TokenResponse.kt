package com.example.proyfronted.backend.Auth.Model

data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val token_type: String
)
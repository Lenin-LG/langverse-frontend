package com.example.proyfronted.backend.Speaking.dto

data class SpeakingPracticeDto(
    val phraseId: Long,
    val spokenText: String,
    val accuracy: Double
)
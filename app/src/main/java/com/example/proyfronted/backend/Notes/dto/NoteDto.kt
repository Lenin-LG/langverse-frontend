package com.example.proyfronted.backend.Notes.dto

data class NoteDto(
    val id: Long? = null,
    val title: String,
    val content: String,
    val notebookId: Long,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

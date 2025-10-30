package com.example.proyfronted.backend.Notes.Client

import com.example.proyfronted.backend.Notes.dto.NoteBookDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NoteBookApi {
    @GET("/notebook")
    suspend fun getMyNotebooks(): List<NoteBookDto>

    @POST("/notebook/create")
    suspend fun createNotebook(@Body dto: NoteBookDto): NoteBookDto
    @DELETE("/notebook/{id}")
    suspend fun deleteNotebook(@Path("id") id: Long): Response<Unit>

}

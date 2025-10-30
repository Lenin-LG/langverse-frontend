package com.example.proyfronted.backend.Notes.Client

import com.example.proyfronted.backend.Notes.dto.NoteDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApi {

    @GET("/note/notebook/{notebookId}")
    suspend fun getNotesByNotebook(@Path("notebookId") notebookId: Long): List<NoteDto>


    @POST("/note")
    suspend fun createNote(@Body note: NoteDto): NoteDto
    @DELETE("/note/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<Unit>
    @PUT("/note/{id}")
    suspend fun updateNote(@Path("id") id: Long, @Body note: NoteDto): NoteDto


}

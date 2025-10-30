package com.example.proyfronted.backend.Speaking.Client

import com.example.proyfronted.backend.Speaking.dto.SpeakingPracticeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpeakingPracticeApi {
    @GET("speaking/me")
    suspend fun getMyPractices(): List<SpeakingPracticeDto>

    @POST("speaking")
    suspend fun savePractice(@Body practice: SpeakingPracticeDto): SpeakingPracticeDto
}
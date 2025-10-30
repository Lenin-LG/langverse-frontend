package com.example.proyfronted.backend.Speaking.Client

import com.example.proyfronted.backend.Speaking.dto.PhraseDto
import retrofit2.http.GET
import retrofit2.http.Path


interface PhraseApi {
    @GET("phrase/topic/{topicId}")
    suspend fun getPhrasesByTopic(@Path("topicId") topicId: Long): List<PhraseDto>
}
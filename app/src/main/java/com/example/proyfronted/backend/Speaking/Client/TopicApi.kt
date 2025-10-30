package com.example.proyfronted.backend.Speaking.Client

import com.example.proyfronted.backend.Speaking.dto.TopicDto
import retrofit2.http.GET

interface TopicApi {
    @GET("topic/all")
    suspend fun getAllTopics(): List<TopicDto>
}
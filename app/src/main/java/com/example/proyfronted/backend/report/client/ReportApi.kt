package com.example.proyfronted.backend.report.client

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface ReportApi {
    @GET("/report/users")
    @Streaming
    suspend fun downloadUserReport(): Response<ResponseBody>
}
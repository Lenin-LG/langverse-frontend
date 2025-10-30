package com.example.proyfronted.backend.Auth.Interface
import com.example.proyfronted.backend.Auth.Model.LoginRequest
import com.example.proyfronted.backend.Auth.Model.RegisterRequest
import com.example.proyfronted.backend.Auth.Model.TokenResponse
import com.example.proyfronted.backend.Auth.Model.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface KeycloakApiService {
    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<TokenResponse>
    @POST("/auth/create")
    suspend fun register(@Body request: RegisterRequest): Response<Void>
    @GET("/auth/me")
    suspend fun getUserInfo(@Header("Authorization") token: String): Response<UserDTO>

    @DELETE("/auth/delete")
    suspend fun deleteUser(@Header("Authorization") token: String): Response<Unit>

    @PUT("/auth/enabled")
    suspend fun setUserEnabled(
        @Header("Authorization") token: String,
        @Body body: Map<String, Boolean>
    ): Response<Unit>

    @PUT("/auth/update")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body body: UserDTO
    ): Response<Unit>

    @POST("/auth/refresh")
    suspend fun refreshToken(@Body body: Map<String, String>): Response<TokenResponse>


}
package com.example.proyfronted.backend.Movie

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movies/by-category/{id}")
    suspend fun getByCategory(@Path("id") categoryId: Long): Response<List<MovieDto>>
    @GET("movies/search-all")
    suspend fun searchMoviesByTitle(@Query("title") title: String): Response<List<MovieDto>>
    @GET("movies/{id}")
    suspend fun getMovieById(@Path("id") id: Long): Response<MovieDto>


    @GET("movies/series/{title}/episodes-all")
    suspend fun getAllEpisodesGrouped(@Path("title") title: String): Response<Map<Int, List<MovieDto>>>
    @GET("movies/play/{id}/{quality}")
    suspend fun getPlaybackUrl(
        @Path("id") movieId: Long,
        @Path("quality") quality: String
    ): Response<String>

    // Obtener los assets: audios, subtítulos, imagen
    @GET("movies/{id}/assets")
    suspend fun getMovieAssets(@Path("id") movieId: Long): Response<MovieAssetsDto>
    @Multipart
    @POST("movies/create/s3")
    suspend fun uploadMovie(
        @Part movieJson: MultipartBody.Part,
        @Part file: MultipartBody.Part,
        @Part audioEn: MultipartBody.Part,
        @Part audioEs: MultipartBody.Part,
        @Part subsEn: MultipartBody.Part,
        @Part subsEs: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): Response<MovieDto>
}

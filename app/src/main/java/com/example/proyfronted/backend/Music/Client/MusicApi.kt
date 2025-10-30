package com.example.proyfronted.backend.Music.Client

import com.example.proyfronted.backend.Music.Dto.AlbumDto
import com.example.proyfronted.backend.Music.Dto.SongDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MusicApi {
    @GET("album/mine")
    suspend fun getMyAlbums(): List<AlbumDto>

    @POST("album")
    suspend fun createAlbum(@Body albumDto: AlbumDto): AlbumDto

    @GET("album/{albumId}/songs")
    suspend fun getSongsByAlbum(@Path("albumId") albumId: Long): List<SongDto>

    @POST("album/albums/{albumId}/add-song/{songId}")
    suspend fun addSongToAlbum(
        @Path("albumId") albumId: Long,
        @Path("songId") songId: Long
    ): Response<Void>

    @GET("song/songs/{id}")
    suspend fun getSongById(@Path("id") songId: Long): SongDto

    @GET("song")
    suspend fun getAllSongs(): List<SongDto>
    @Multipart
    @POST("song/songs/upload")
    suspend fun uploadSong(
        @Part("title") title: RequestBody,
        @Part("duration") duration: RequestBody,
        @Part audioFile: MultipartBody.Part,
        @Part lyricsEs: MultipartBody.Part?,
        @Part lyricsEn: MultipartBody.Part?
    ): Response<SongDto>

}

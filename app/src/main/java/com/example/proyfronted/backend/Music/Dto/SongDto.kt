package com.example.proyfronted.backend.Music.Dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongDto(
    val idSong: Long,
    val title: String,
    val audioUrl: String,
    val lyricsEs: String?,
    val lyricsEn: String?,
    val idAlbum: Long?,
    val durationInSeconds: Int,
    val visible: Boolean = true
) : Parcelable

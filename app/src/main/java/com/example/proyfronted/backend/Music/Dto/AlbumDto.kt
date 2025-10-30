package com.example.proyfronted.backend.Music.Dto




data class AlbumDto(
    val idAlbum: Long? = null,
    val title: String,
    val description: String? = null,
    val imageCover: String? = null,
    val visible: Boolean = true,
    val songs: List<SongDto>? = null,
    val songIds: List<Long> = emptyList(),
    val userId: String? = null
)

package com.example.proyfronted.backend.Movie

data class MovieDto(
    val idMovie: Long? = null,
    val title: String,
    val description: String? = null,
    val idCategory: Long,
    val videoUrl480p: String? = null,
    val videoUrl720p: String? = null,
    val videoUrl1080p: String? = null,
    val audioUrlEn: String? = null,
    val audioUrlEs: String? = null,
    val subTitlesEnglish: String? = null,
    val subTitlesSpanish: String? = null,
    val durationInMinutes: Int,
    val releaseDate: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val imageBanner: String? = null,
    val estate: Boolean = true
)

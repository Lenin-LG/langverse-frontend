package com.example.proyfronted.backend.Movie

sealed class EpisodeItem {
    data class Header(val movie: MovieDto) : EpisodeItem()
    data class SeasonHeader(val season: Int) : EpisodeItem()
    data class Episode(val movie: MovieDto) : EpisodeItem()
}
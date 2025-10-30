package com.example.proyfronted.backend.Movie

object MovieAssetLoader {
    suspend fun load(movieId: Long, quality: String, audio: String, sub: String): Triple<String, String, String?> {
        val assetResponse = RetrofitClient.api.getMovieAssets(movieId)
        val videoResponse = RetrofitClient.api.getPlaybackUrl(movieId, quality)

        if (!assetResponse.isSuccessful || !videoResponse.isSuccessful) {
            throw Exception("Error al obtener recursos")
        }

        val assets = assetResponse.body()!!
        val videoUrl = videoResponse.body()!!
        val audioUrl = if (audio == "es") assets.audioEsUrl else assets.audioEnUrl
        val subtitleUrl = if (sub == "es") assets.subtitlesEsUrl else assets.subtitlesEnUrl

        return Triple(videoUrl, audioUrl, subtitleUrl)
    }
}

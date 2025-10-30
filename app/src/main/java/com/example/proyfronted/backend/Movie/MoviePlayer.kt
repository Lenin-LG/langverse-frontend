package com.example.proyfronted.backend.Movie

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.source.SingleSampleMediaSource
import androidx.media3.ui.PlayerView

@OptIn(androidx.media3.common.util.UnstableApi::class)
class MoviePlayer(
    private val context: Context,
    private val playerView: PlayerView
) {
    private var player: ExoPlayer? = null
    private var currentPosition: Long = 0

    fun initialize(videoUrl: String, audioUrl: String, subtitleUrl: String?, language: String, resumeFrom: Long = 0L) {
        release()

        player = ExoPlayer.Builder(context).build().also { exoPlayer ->
            playerView.player = exoPlayer
            playerView.setShowSubtitleButton(true)

            val dataSourceFactory = DefaultDataSource.Factory(context)

            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl.toUri()))

            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(audioUrl.toUri()))

            val subtitleSource = subtitleUrl?.let {
                val config = MediaItem.SubtitleConfiguration.Builder(it.toUri())
                    .setMimeType(MimeTypes.TEXT_VTT)
                    .setLanguage(language)
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()

                SingleSampleMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(config, C.TIME_UNSET)
            }

            val mergedSource = if (subtitleSource != null) {
                MergingMediaSource(videoSource, audioSource, subtitleSource)
            } else {
                MergingMediaSource(videoSource, audioSource)
            }

            exoPlayer.setMediaSource(mergedSource)
            exoPlayer.prepare()
            exoPlayer.seekTo(resumeFrom)
            exoPlayer.playWhenReady = true
        }
    }

    fun getCurrentPosition(): Long {
        val pos = player?.currentPosition ?: 0L
        return pos
    }
    fun pause() = player?.pause()
    fun release() {
        currentPosition = getCurrentPosition()
        player?.release()
        player = null
    }
}
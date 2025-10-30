package com.example.proyfronted.ui.music.play

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.R
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LoadControl

@androidx.media3.common.util.UnstableApi
class PlayFragment : Fragment() {

    private lateinit var exoPlayer: ExoPlayer
    private val lrcManager = LrcManager()

    private lateinit var txtSongTitle: TextView
    private lateinit var lrcTextView: TextView
    private lateinit var lrcTextViewSpanish: TextView

    private lateinit var seekBar: SeekBar
    private lateinit var btnTogglePlay: Button
    private lateinit var btnStop: Button
    private lateinit var btnRewind: Button
    private lateinit var btnForward: Button

    private lateinit var handler: Handler
    private var isPlaying = false

    private var lrcLines: List<LrcLine> = emptyList()
    private var lrcLinesEs: List<LrcLine> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fondo animado
        val container = view.findViewById<View>(R.id.playerContainer)
        (container.background as? android.graphics.drawable.AnimationDrawable)?.apply {
            setEnterFadeDuration(2000)
            setExitFadeDuration(4000)
            start()
        }

        // Inicializar vistas
        txtSongTitle = view.findViewById(R.id.txtSongTitle)
        lrcTextView = view.findViewById(R.id.lrcTextView)
        lrcTextViewSpanish = view.findViewById(R.id.lrcTextViewSpanish)

        seekBar = view.findViewById(R.id.seekBar)
        btnTogglePlay = view.findViewById(R.id.btnTogglePlay)
        btnStop = view.findViewById(R.id.btnStop)
        btnRewind = view.findViewById(R.id.btnRewind)
        btnForward = view.findViewById(R.id.btnForward)

        handler = Handler(Looper.getMainLooper())

        // Inicializar ExoPlayer
        val loadControl: LoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                25000,  // minBufferMs: cuánto tiempo debe cargar antes de reproducir
                50000,  // maxBufferMs: cuánto puede almacenar como máximo
                1500,   // bufferForPlaybackMs: cuánto necesita para comenzar la reproducción
                2000    // bufferForPlaybackAfterRebufferMs: cuánto necesita tras una pausa por buffering
            )
            .build()

        exoPlayer = ExoPlayer.Builder(requireContext())
            .setLoadControl(loadControl)
            .build()


        // Botón de play/pausa
        btnTogglePlay.setOnClickListener {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                isPlaying = false
                btnTogglePlay.text = "▶️"
            } else {
                exoPlayer.play()
                isPlaying = true
                btnTogglePlay.text = "⏸"
            }
        }

        // Botón de stop
        btnStop.setOnClickListener {
            exoPlayer.seekTo(0)
            exoPlayer.pause()
            isPlaying = false
            btnTogglePlay.text = "▶️"
        }

        // Retroceso 5s
        btnRewind.setOnClickListener {
            val newPos = (exoPlayer.currentPosition - 5000).coerceAtLeast(0)
            exoPlayer.seekTo(newPos)
        }

        // Adelanto 5s
        btnForward.setOnClickListener {
            val newPos = (exoPlayer.currentPosition + 5000).coerceAtMost(exoPlayer.duration)
            exoPlayer.seekTo(newPos)
        }

        // SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) exoPlayer.seekTo(progress.toLong())
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    fun updateSong(songDto: SongDto) {
        txtSongTitle.text = songDto.title

        lrcManager.downloadLrc(songDto.lyricsEn) { lines ->
            lrcLines = lines
        }

        lrcManager.downloadLrc(songDto.lyricsEs) { lines ->
            lrcLinesEs = lines
        }

        setupExoPlayer(songDto.audioUrl)
    }

    private fun setupExoPlayer(url: String?) {
        if (url.isNullOrEmpty()) return

        exoPlayer.clearMediaItems()
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        isPlaying = true
        btnTogglePlay.text = "⏸"

        startLrcSync()
    }

    private fun startLrcSync() {
        handler.post(object : Runnable {
            override fun run() {
                val currentPos = exoPlayer.currentPosition

                seekBar.max = exoPlayer.duration.toInt()
                seekBar.progress = currentPos.toInt()

                val currentLineEn = lrcLines.lastOrNull { it.timeMillis <= currentPos }
                val currentLineEs = lrcLinesEs.lastOrNull { it.timeMillis <= currentPos }

                currentLineEn?.let {
                    val span = SpannableString(it.text)
                    span.setSpan(StyleSpan(Typeface.BOLD), 0, it.text.length, 0)
                    span.setSpan(ForegroundColorSpan(Color.GREEN), 0, it.text.length, 0)
                    lrcTextView.text = span
                }

                currentLineEs?.let {
                    val span = SpannableString(it.text)
                    span.setSpan(StyleSpan(Typeface.BOLD), 0, it.text.length, 0)
                    span.setSpan(ForegroundColorSpan(Color.rgb(255, 111, 0)), 0, it.text.length, 0)
                    lrcTextViewSpanish.text = span
                }

                handler.postDelayed(this, 300)
            }
        })
    }

    private fun stopLrcSync() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLrcSync()
        exoPlayer.release()
    }

    data class LrcLine(val timeMillis: Long, val text: String)
}

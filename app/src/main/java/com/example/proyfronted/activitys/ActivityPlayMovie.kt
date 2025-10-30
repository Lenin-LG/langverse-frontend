package com.example.proyfronted.activitys

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.backend.Movie.CachedMovieAssets
import com.example.proyfronted.backend.Movie.MoviePlayer
import com.example.proyfronted.backend.Movie.RetrofitClient.api
import com.example.proyfronted.databinding.ActivityPlayMovieBinding
import kotlinx.coroutines.*

class ActivityPlayMovie : AppCompatActivity() {

    private val assetsCache = mutableMapOf<Long, CachedMovieAssets>()
    private val videoUrlCache = mutableMapOf<String, String>() // key = movieId + quality

    private lateinit var binding: ActivityPlayMovieBinding
    private lateinit var moviePlayer: MoviePlayer

    private var movieId: Long = -1
    private var currentQuality = "480p"
    private var currentAudio = "en"
    private var currentSub = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getLongExtra("movieId", -1)
        moviePlayer = MoviePlayer(this, binding.playerView)

        setupSpinners()
        setupListeners()

        if (movieId != -1L) loadAssetsAndPlay()
    }

    private fun setupSpinners() {
        val audioOptions = listOf("en", "es")
        val subtitleOptions = listOf("en", "es")
        val qualityOptions = listOf("480p", "720p", "1080p")

        binding.spinnerAudio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, audioOptions)
        binding.spinnerSubtitles.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subtitleOptions)
        binding.spinnerQuality.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, qualityOptions)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        val reload = { loadAssetsAndPlay() }

        binding.spinnerAudio.onItemSelectedListener = spinnerListener { currentAudio = it; reload() }
        binding.spinnerSubtitles.onItemSelectedListener = spinnerListener { currentSub = it; reload() }
        binding.spinnerQuality.onItemSelectedListener = spinnerListener { currentQuality = it; reload() }
    }

    private fun spinnerListener(action: (String) -> Unit) = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
            action(parent.getItemAtPosition(pos).toString())
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    private fun loadAssetsAndPlay() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resumeFrom = withContext(Dispatchers.Main) {
                    val position = moviePlayer.getCurrentPosition()
                    position
                }

                val videoUrl = getCachedVideoUrl(movieId, currentQuality)

                val assets = assetsCache[movieId] ?: run {
                    val response = api.getMovieAssets(movieId)
                    val data = response.body()!!

                    val cached = CachedMovieAssets(
                        audioEs = data.audioEsUrl,
                        audioEn = data.audioEnUrl,
                        subEs = data.subtitlesEsUrl,
                        subEn = data.subtitlesEnUrl
                    )
                    assetsCache[movieId] = cached
                    cached
                }

                val audioUrl = if (currentAudio == "es") assets.audioEs else assets.audioEn
                val subUrl = if (currentSub == "es") assets.subEs else assets.subEn

                withContext(Dispatchers.Main) {
                    moviePlayer.initialize(videoUrl, audioUrl, subUrl, currentSub, resumeFrom)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ActivityPlayMovie, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private suspend fun getCachedVideoUrl(id: Long, quality: String): String {
        val key = "$id-$quality"
        return videoUrlCache[key] ?: run {
            val response = api.getPlaybackUrl(id, quality)
            if (!response.isSuccessful) throw Exception("Could not get video URL")

            val url = response.body()!!
            videoUrlCache[key] = url
            url
        }
    }

    override fun onPause() {
        super.onPause()
        moviePlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        moviePlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        moviePlayer.release()
    }
}

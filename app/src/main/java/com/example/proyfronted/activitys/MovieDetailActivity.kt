package com.example.proyfronted.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyfronted.backend.Movie.EpisodeAdapter
import com.example.proyfronted.backend.Movie.EpisodeItem
import com.example.proyfronted.backend.Movie.RetrofitClient
import com.example.proyfronted.databinding.ActivityMovieDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding
    private var movieId: Long = -1
    private lateinit var adapter: EpisodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getLongExtra("movieId", -1)

        adapter = EpisodeAdapter(
            onEpisodeClick = { episode ->
                val intent = Intent(this@MovieDetailActivity, ActivityPlayMovie::class.java)
                intent.putExtra("movieId", episode.idMovie)
                startActivity(intent)
            },
            onPlayClick = { headerMovie ->
                // Ir al primer episodio de la temporada 1
                CoroutineScope(Dispatchers.IO).launch {
                    val response = RetrofitClient.api.getAllEpisodesGrouped(headerMovie.title)
                    if (response.isSuccessful) {
                        val grouped = response.body() ?: emptyMap()
                        val firstEpisode = grouped[1]?.firstOrNull()
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@MovieDetailActivity, ActivityPlayMovie::class.java)
                            intent.putExtra("movieId", firstEpisode?.idMovie ?: headerMovie.idMovie)
                            startActivity(intent)
                        }
                    }
                }
            }
        )

        binding.recyclerDetail.layoutManager = LinearLayoutManager(this)
        binding.recyclerDetail.adapter = adapter

        if (movieId != -1L) {
            fetchMovieDetails()
        }
    }

    private fun fetchMovieDetails() {
        CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.api.getMovieById(movieId)
                if (response.isSuccessful) {
                    val movie = response.body()!!
                    val items = mutableListOf<EpisodeItem>()

                    items.add(EpisodeItem.Header(movie))

                    if (movie.idCategory == 2L && movie.seasonNumber != null) {
                        val episodesResp = RetrofitClient.api.getAllEpisodesGrouped(movie.title)
                        if (episodesResp.isSuccessful) {
                            val grouped = episodesResp.body() ?: emptyMap()
                            for ((season, episodes) in grouped) {
                                items.add(EpisodeItem.SeasonHeader(season))
                                items.addAll(episodes.map { EpisodeItem.Episode(it) })
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        adapter.submitItems(items)
                    }
                }

        }
    }
}

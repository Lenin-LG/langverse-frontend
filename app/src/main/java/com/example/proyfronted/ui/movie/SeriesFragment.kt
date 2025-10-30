package com.example.proyfronted.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyfronted.activitys.MovieDetailActivity
import com.example.proyfronted.backend.Movie.RetrofitClient
import com.example.proyfronted.backend.Movie.MovieAdapter
import com.example.proyfronted.backend.Movie.MovieDto
import com.example.proyfronted.databinding.FragmentSeriesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeriesFragment : Fragment(), Searchable {

    private lateinit var binding: FragmentSeriesBinding
    private lateinit var adapter: MovieAdapter
    private var allSeries: List<MovieDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MovieAdapter { movie ->
            openMovieDetail(movie.idMovie ?: -1L)
        }

        binding.recyclerMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMovies.adapter = adapter

        fetchSeries()

        arguments?.getString("search")?.let { onSearchChanged(it) }
    }

    private fun fetchSeries() {
        CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.api.getByCategory(2)
                if (response.isSuccessful) {
                    allSeries = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.submitList(allSeries)
                    }
                }
        }
    }

    override fun onSearchChanged(query: String) {
        val filtered = allSeries.filter {
            it.title.contains(query, ignoreCase = true)
        }
        adapter.submitList(filtered)
    }

    private fun openMovieDetail(id: Long) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra("movieId", id)
        startActivity(intent)
    }
}

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
import com.example.proyfronted.databinding.FragmentPeliculasBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PeliculasFragment : Fragment(), Searchable {

    private lateinit var binding: FragmentPeliculasBinding
    private lateinit var adapter: MovieAdapter
    private var allMovies: List<MovieDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeliculasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MovieAdapter { movie ->
            movie.idMovie?.let { id ->
                openMovieDetail(id)
            }
        }

        binding.recyclerMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMovies.adapter = adapter

        fetchMovies()

        arguments?.getString("search")?.let { onSearchChanged(it) }
    }

    private fun fetchMovies() {
        CoroutineScope(Dispatchers.IO).launch {

                val response = RetrofitClient.api.getByCategory(1)
                if (response.isSuccessful) {
                    val movies = response.body() ?: emptyList()
                    allMovies = movies
                    withContext(Dispatchers.Main) {
                        adapter.submitList(allMovies)
                    }
                }

        }
    }

    private fun openMovieDetail(id: Long) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra("movieId", id)
        startActivity(intent)
    }

    override fun onSearchChanged(query: String) {
        val filtered = allMovies.filter {
            it.title.contains(query, ignoreCase = true)
        }
        adapter.submitList(filtered)
    }
}

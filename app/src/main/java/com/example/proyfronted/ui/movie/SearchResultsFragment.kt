package com.example.proyfronted.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyfronted.activitys.MovieDetailActivity
import com.example.proyfronted.backend.Movie.MovieAdapter
import com.example.proyfronted.backend.Movie.RetrofitClient
import com.example.proyfronted.databinding.FragmentAllMoviesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultsFragment : Fragment() {

    private lateinit var binding: FragmentAllMoviesBinding
    private lateinit var adapter: MovieAdapter
    private var searchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MovieAdapter { movie ->
            openMovieDetail(movie.idMovie ?: -1L)
        }

        binding.recyclerAllMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAllMovies.adapter = adapter

        arguments?.getString("searchQuery")?.let {
            searchText = it
            fetchSearchResults(searchText)
        }
    }

    private fun openMovieDetail(id: Long) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra("movieId", id)
        startActivity(intent)
    }

    private fun fetchSearchResults(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.api.searchMoviesByTitle(query)
                if (response.isSuccessful) {
                    val results = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.submitList(results)
                    }
                }
        }
    }
}


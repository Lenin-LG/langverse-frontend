package com.example.proyfronted.activitys

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.proyfronted.backend.Auth.utils.refreshAccessToken
import com.example.proyfronted.R
import com.example.proyfronted.databinding.ActivityMovieBinding
import com.example.proyfronted.ui.movie.PeliculasFragment
import com.example.proyfronted.ui.movie.SearchResultsFragment
import com.example.proyfronted.ui.movie.SeriesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MovieActivity : AppCompatActivity() {
    private var tokenRefreshJob: Job? = null

    private lateinit var binding: ActivityMovieBinding
    private var currentFragment: Fragment? = null
    private var currentSearchText: String = ""
    private var currentTab: Fragment = PeliculasFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTokenRefreshJob()
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Initial load
        showMainFragment(PeliculasFragment())

        binding.btnPeliculas.setOnClickListener {
            binding.etSearch.setText("") // clean search
            showMainFragment(PeliculasFragment())
        }

        binding.btnSeries.setOnClickListener {
            binding.etSearch.setText("") // clean search
            showMainFragment(SeriesFragment())
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s.toString()

                if (currentSearchText.isBlank()) {
                    showMainFragment(currentTab)
                } else {
                    showSearchResultsFragment(currentSearchText)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showMainFragment(fragment: Fragment) {
        currentTab = fragment
        currentFragment = fragment
        fragment.arguments = Bundle().apply {
            putString("search", currentSearchText)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showSearchResultsFragment(query: String) {
        val fragment = SearchResultsFragment().apply {
            arguments = Bundle().apply {
                putString("searchQuery", query)
            }
        }
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun startTokenRefreshJob() {
        tokenRefreshJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(4 * 60 * 1000) // every 4 minutes
                refreshAccessToken(applicationContext)
            }
        }
    }
}

package com.example.proyfronted.activitys

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.proyfronted.backend.Auth.utils.refreshAccessToken
import com.example.proyfronted.backend.Music.Client.RetrofitClient
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Music.MusicPagerAdapter
import com.example.proyfronted.ui.music.play.PlayFragment
import com.example.proyfronted.ui.music.play.PlaylistFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
@androidx.media3.common.util.UnstableApi
class MusicActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var tokenRefreshJob: Job? = null

    private val playFragment = PlayFragment()
    private val playlistFragment = PlaylistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        startTokenRefreshJob()

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = MusicPagerAdapter(this, playFragment, playlistFragment)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Álbumes"
                1 -> "Lista"
                2 -> "Reproductor"
                else -> null
            }
        }.attach()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenRefreshJob?.cancel()
    }

    private fun startTokenRefreshJob() {
        tokenRefreshJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(4 * 60 * 1000)
                refreshAccessToken(applicationContext)
            }
        }
    }

    fun playSong(songDto: SongDto) {
        lifecycleScope.launch {
            val fullSong = RetrofitClient.musicApi.getSongById(songDto.idSong)
            playFragment.updateSong(fullSong)
            viewPager.currentItem = 2
        }
    }

    fun refreshPlaylistAfterAdding(albumId: Long) {
        lifecycleScope.launch {
            val albumName = getAlbumNameById(albumId)
            val songs = RetrofitClient.musicApi.getSongsByAlbum(albumId)

            val adapter = viewPager.adapter as? MusicPagerAdapter
            adapter?.updatePlaylistArguments(ArrayList(songs), albumName, albumId)

            playlistFragment.updateSongs(songs, albumName)
        }
    }

    suspend fun getAlbumNameById(albumId: Long): String {
        return try {
            val albums = RetrofitClient.musicApi.getMyAlbums()
            albums.find { it.idAlbum == albumId }?.title ?: "Álbum"
        } catch (e: Exception) {
            "Álbum"
        }
    }

    fun getViewPager(): ViewPager2 = viewPager
}

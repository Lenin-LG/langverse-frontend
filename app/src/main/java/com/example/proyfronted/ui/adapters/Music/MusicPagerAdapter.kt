package com.example.proyfronted.ui.adapters.Music

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.ui.music.albums.AlbumsFragment
import com.example.proyfronted.ui.music.play.PlayFragment
import com.example.proyfronted.ui.music.play.PlaylistFragment
@UnstableApi
class MusicPagerAdapter(
    activity: FragmentActivity,
    private val playFragment: PlayFragment,
    private val playlistFragment: PlaylistFragment
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlbumsFragment()
            1 -> playlistFragment
            2 -> playFragment
            else -> throw IllegalArgumentException("Posición inválida del ViewPager")
        }
    }

    fun updatePlaylistArguments(songs: ArrayList<SongDto>, albumName: String, albumId: Long) {
        playlistFragment.applyArguments(songs, albumName, albumId)
    }
}


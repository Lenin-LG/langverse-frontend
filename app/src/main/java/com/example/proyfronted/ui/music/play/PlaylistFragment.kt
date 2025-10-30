package com.example.proyfronted.ui.music.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.activitys.MusicActivity
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Music.SongsAdapter
import com.example.proyfronted.ui.music.albums.SongSelectorFragment

@androidx.media3.common.util.UnstableApi
class PlaylistFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var emptyText: TextView? = null
    private var btnAddSongs: Button? = null
    private var txtPlaylistTitle: TextView? = null
    private var albumId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_playlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtPlaylistTitle = view.findViewById(R.id.txtPlaylistTitle)
        recyclerView = view.findViewById(R.id.recyclerViewSongs)
        emptyText = view.findViewById(R.id.txtNoSongs)
        btnAddSongs = view.findViewById(R.id.btnAddSongsToAlbum)

        // Recuperar argumentos iniciales si existen
        val songs = arguments?.getParcelableArrayList<SongDto>("songs") ?: ArrayList()
        val albumName = arguments?.getString("album_name") ?: "Todas las canciones"
        albumId = arguments?.getLong("album_id", -1L) ?: -1L

        txtPlaylistTitle?.text = albumName
        renderSongs(songs)

        configureAddSongsButton()
    }

    fun applyArguments(songs: ArrayList<SongDto>, albumName: String, albumId: Long) {
        this.albumId = albumId
        txtPlaylistTitle?.text = albumName
        renderSongs(songs)
        configureAddSongsButton()
    }

    private fun configureAddSongsButton() {
        if (albumId > 0) {
            btnAddSongs?.visibility = View.VISIBLE
            btnAddSongs?.setOnClickListener {
                val selectorFragment = SongSelectorFragment(albumId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentOverlayContainer, selectorFragment)
                    .addToBackStack(null)
                    .commit()
                requireActivity().findViewById<View>(R.id.fragmentOverlayContainer)?.visibility = View.VISIBLE
            }
        } else {
            btnAddSongs?.visibility = View.GONE
        }
    }

    fun updateSongs(newSongs: List<SongDto>, albumName: String) {
        txtPlaylistTitle?.text = albumName
        renderSongs(newSongs)
    }

    private fun renderSongs(songs: List<SongDto>) {
        if (songs.isEmpty()) {
            emptyText?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            emptyText?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            recyclerView?.adapter = SongsAdapter(songs) { song ->
                (requireActivity() as? MusicActivity)?.playSong(song)
            }
        }
    }
}

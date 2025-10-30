package com.example.proyfronted.ui.music.albums

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.activitys.MusicActivity
import com.example.proyfronted.backend.Music.Client.RetrofitClient
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Music.SelectableSongsAdapter
import kotlinx.coroutines.launch

class SongSelectorFragment(private val albumId: Long) : Fragment() {

    private val selectedSongs = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_song_selector, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAllSongs)
        val btnAdd = view.findViewById<Button>(R.id.btnAddSelectedSongs)
        val btnBack = view.findViewById<Button>(R.id.btnBackSelector)

        lifecycleScope.launch {
            try {
                val allSongs = RetrofitClient.musicApi.getAllSongs()
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = SelectableSongsAdapter(allSongs) { song, selected ->
                    if (selected) selectedSongs.add(song.idSong)
                    else selectedSongs.remove(song.idSong)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar canciones", Toast.LENGTH_SHORT).show()
            }
        }

        btnAdd.setOnClickListener {
            lifecycleScope.launch {
                try {
                    selectedSongs.forEach {

                        Log.d("SongSelectorFragment", "Añadiendo canción con ID: $it")
                        RetrofitClient.musicApi.addSongToAlbum(albumId, it)
                    }

                    Log.d("SongSelectorFragment", "Canciones añadidas exitosamente")
                    Toast.makeText(requireContext(), "Canciones añadidas", Toast.LENGTH_SHORT).show()

                    (requireActivity() as? MusicActivity)?.refreshPlaylistAfterAdding(albumId) // 👈 Asegúrate de tener esto

                    cerrarFragmento()
                } catch (e: Exception) {
                    Log.e("SongSelectorFragment", "Error al agregar canciones", e)   }
            }
        }


        btnBack.setOnClickListener {
            cerrarFragmento()
        }
    }

    private fun cerrarFragmento() {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().findViewById<View>(R.id.fragmentOverlayContainer)?.visibility = View.GONE
    }
}
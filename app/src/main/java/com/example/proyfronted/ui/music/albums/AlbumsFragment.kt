package com.example.proyfronted.ui.music.albums
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.activitys.MusicActivity
import com.example.proyfronted.backend.Music.Client.RetrofitClient
import com.example.proyfronted.backend.Music.Dto.AlbumDto
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Music.AlbumsAdapter
import com.example.proyfronted.ui.adapters.Music.MusicPagerAdapter
import kotlinx.coroutines.launch
@androidx.media3.common.util.UnstableApi
class AlbumsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumsAdapter: AlbumsAdapter
    private val albums: MutableList<AlbumDto> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_albums, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerViewAlbums)
        loadAlbums()
    }

    private fun loadAlbums() {
        lifecycleScope.launch {
            try {
                val albumsResponse = RetrofitClient.musicApi.getMyAlbums()
                albums.clear()
                albums.addAll(albumsResponse)

                // Agregar opción "Crear nuevo álbum"
                albums.add(AlbumDto(idAlbum = -1, title = "Crear nuevo álbum", songIds = emptyList()))

                albumsAdapter = AlbumsAdapter(albums) { selectedAlbum ->
                    if (selectedAlbum.idAlbum == -1L) {
                        showCreateAlbumDialog()
                    } else {
                        navigateToAlbumSongs(selectedAlbum)
                    }
                }

                recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                recyclerView.adapter = albumsAdapter

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar álbumes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCreateAlbumDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_album, null)
        val editText = dialogView.findViewById<EditText>(R.id.etAlbumName)

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo Álbum")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val albumName = editText.text.toString()
                if (albumName.isNotBlank()) {
                    lifecycleScope.launch {
                        try {
                            val newAlbum = AlbumDto(title = albumName, songIds = emptyList())
                            val createdAlbum = RetrofitClient.musicApi.createAlbum(newAlbum)

                            // Reemplazar botón "Crear nuevo álbum"
                            albums.removeLastOrNull()
                            albums.add(createdAlbum)
                            albums.add(AlbumDto(idAlbum = -1, title = "Crear nuevo álbum", songIds = emptyList()))

                            albumsAdapter.notifyDataSetChanged()
                            navigateToAlbumSongs(createdAlbum)

                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error al crear álbum", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun navigateToAlbumSongs(albumDto: AlbumDto) {
        lifecycleScope.launch {
            try {
                val songs: List<SongDto> = RetrofitClient.musicApi.getSongsByAlbum(albumDto.idAlbum!!)
                val songList = ArrayList(songs)

                val activity = requireActivity() as? MusicActivity
                val viewPager = activity?.getViewPager()
                val adapter = viewPager?.adapter as? MusicPagerAdapter

                viewPager?.currentItem = 1
                viewPager?.post {
                    adapter?.updatePlaylistArguments(songList, albumDto.title, albumDto.idAlbum!!)
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al obtener canciones", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

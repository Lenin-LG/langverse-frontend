package com.example.proyfronted.ui.adapters.Music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.R

class SelectableSongsAdapter(
    private val songs: List<SongDto>,
    private val onItemSelected: (SongDto, Boolean) -> Unit
) : RecyclerView.Adapter<SelectableSongsAdapter.ViewHolder>() {

    private val selectedSongs = mutableSetOf<Long>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.songTitle)
        private val checkBox = view.findViewById<CheckBox>(R.id.songCheckbox)

        fun bind(song: SongDto) {
            title.text = song.title
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = selectedSongs.contains(song.idSong)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedSongs.add(song.idSong)
                } else {
                    selectedSongs.remove(song.idSong)
                }
                onItemSelected(song, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song_selectable, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    fun getSelectedSongs(): List<Long> = selectedSongs.toList()
}

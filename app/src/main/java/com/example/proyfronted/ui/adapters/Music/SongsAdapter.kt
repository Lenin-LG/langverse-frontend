package com.example.proyfronted.ui.adapters.Music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Music.Dto.SongDto
import com.example.proyfronted.R

class SongsAdapter(
    private val songs: List<SongDto>,
    private val onItemClick: (SongDto) -> Unit
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.songTitle)
        private val artistView: TextView = itemView.findViewById(R.id.songArtist)
        //  private val durationView: TextView = itemView.findViewById(R.id.songDuration) //

        fun bind(song: SongDto) {
            titleView.text = song.title
            artistView.text = "Desconocido" // No  artist en el DTO
            itemView.setOnClickListener { onItemClick(song) }
        }
    }
}
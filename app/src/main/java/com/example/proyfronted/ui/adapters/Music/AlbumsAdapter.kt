package com.example.proyfronted.ui.adapters.Music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Music.Dto.AlbumDto
import com.example.proyfronted.R

class AlbumsAdapter(
    private val albums: List<AlbumDto>,
    private val onItemClick: (AlbumDto) -> Unit
) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imgAlbumCover)
        private val textView: TextView = view.findViewById(R.id.txtAlbumName)

        fun bind(album: AlbumDto) {
            imageView.setImageResource(R.drawable.ic_album_default) // o icono dinámico si hay
            textView.text = album.title
            itemView.setOnClickListener { onItemClick(album) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size
}
package com.example.proyfronted.backend.Movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyfronted.R
import com.example.proyfronted.databinding.ItemMovieBinding

class EpisodeAdapter(
    private val onEpisodeClick: ((MovieDto) -> Unit)? = null,
    private val onPlayClick: ((MovieDto) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<EpisodeItem>()

    fun submitItems(newItems: List<EpisodeItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EpisodeItem.Header -> 0
            is EpisodeItem.SeasonHeader -> 1
            is EpisodeItem.Episode -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_detail_header, parent, false)
                HeaderViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_season_header, parent, false)
                SeasonHeaderViewHolder(view)
            }
            else -> {
                val binding = ItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                EpisodeViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EpisodeItem.Header -> (holder as HeaderViewHolder).bind(item.movie)
            is EpisodeItem.SeasonHeader -> (holder as SeasonHeaderViewHolder).bind(item)
            is EpisodeItem.Episode -> (holder as EpisodeViewHolder).bind(item.movie)
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: MovieDto) {
            itemView.findViewById<TextView>(R.id.tvTitle).text = movie.title
            itemView.findViewById<TextView>(R.id.tvDescription).text = movie.description
            itemView.findViewById<TextView>(R.id.tvDuration).text = "${movie.durationInMinutes} min"
            itemView.findViewById<TextView>(R.id.tvRelease).text = "Estreno: ${movie.releaseDate}"

            Glide.with(itemView.context)
                .load(movie.imageBanner)
                .placeholder(R.drawable.baner_error)
                .into(itemView.findViewById(R.id.ivBanner))

            itemView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                (itemView.context as? Activity)?.finish()
            }

            itemView.findViewById<Button>(R.id.btnPlay).setOnClickListener {
                onPlayClick?.invoke(movie)
            }
        }
    }


    inner class SeasonHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(header: EpisodeItem.SeasonHeader) {
            itemView.findViewById<TextView>(R.id.tvSeasonTitle).text = "Temporada ${header.season}"
        }
    }

    inner class EpisodeViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieDto) {
            val titleWithNumber = if (movie.episodeNumber != null) {
                "Episodio ${movie.episodeNumber} - ${movie.title}"
            } else {
                movie.title
            }
            binding.tvTitle.text = titleWithNumber
            Glide.with(binding.root.context)
                .load(movie.imageBanner)
                .placeholder(R.drawable.baner_error)
                .into(binding.ivBanner)
            binding.root.setOnClickListener {
                onEpisodeClick?.invoke(movie)
            }
        }
    }
}

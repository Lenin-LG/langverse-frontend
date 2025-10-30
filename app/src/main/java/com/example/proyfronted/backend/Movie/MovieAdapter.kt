package com.example.proyfronted.backend.Movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyfronted.R
import com.example.proyfronted.databinding.ItemMovieBinding


class MovieAdapter(
    private val onItemClick: ((MovieDto) -> Unit)? = null
) : ListAdapter<MovieDto, MovieAdapter.MovieViewHolder>(DiffCallback()) {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieDto) {
            binding.tvTitle.text = movie.title

            Glide.with(binding.root.context)
                .load(movie.imageBanner)
                .placeholder(R.drawable.baner_error)
                .into(binding.ivBanner)

            binding.root.setOnClickListener {
                onItemClick?.invoke(movie)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieDto>() {
        override fun areItemsTheSame(oldItem: MovieDto, newItem: MovieDto): Boolean =
            oldItem.idMovie == newItem.idMovie

        override fun areContentsTheSame(oldItem: MovieDto, newItem: MovieDto): Boolean =
            oldItem == newItem
    }
}

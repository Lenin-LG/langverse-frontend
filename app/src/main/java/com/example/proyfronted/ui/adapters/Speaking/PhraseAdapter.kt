package com.example.proyfronted.ui.adapters.Speaking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Speaking.dto.PhraseDto
import com.example.proyfronted.R

class PhraseAdapter(
    private val phrases: List<PhraseDto>,
    private val onClick: (PhraseDto) -> Unit
) : RecyclerView.Adapter<PhraseAdapter.PhraseViewHolder>() {

    inner class PhraseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPhrase: TextView = view.findViewById(R.id.txtPhrase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phrase, parent, false)
        return PhraseViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val phrase = phrases[position]
        holder.txtPhrase.text = phrase.text
        holder.itemView.setOnClickListener {
            onClick(phrase)
        }
    }

    override fun getItemCount(): Int = phrases.size
}

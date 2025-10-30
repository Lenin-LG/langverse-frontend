package com.example.proyfronted.ui.adapters.Notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Notes.dto.NoteDto
import com.example.proyfronted.R
class NoteAdapter(
    private val notes: MutableList<NoteDto>,
    private val onDelete: (NoteDto, Int) -> Unit,
    private val onEdit: (NoteDto) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtContent: TextView = view.findViewById(R.id.txtContent)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteNote)

        init {
            btnDelete.setOnClickListener {
                val note = notes[adapterPosition]
                onDelete(note, adapterPosition)
            }

            itemView.setOnClickListener {
                val note = notes[adapterPosition]
                onEdit(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.txtTitle.text = note.title
        holder.txtContent.text = note.content
    }

    override fun getItemCount(): Int = notes.size

    fun removeAt(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }
}


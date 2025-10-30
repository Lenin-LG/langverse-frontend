package com.example.proyfronted.ui.adapters.Notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Notes.dto.NoteBookDto
import com.example.proyfronted.R

class NotebookAdapter(
    private val notebooks: MutableList<NoteBookDto>,
    private val onClick: (NoteBookDto) -> Unit,
    private val onDelete: (NoteBookDto, Int) -> Unit
) : RecyclerView.Adapter<NotebookAdapter.NotebookViewHolder>() {

    inner class NotebookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.txtNotebookTitle)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteNotebook)

        init {
            itemView.setOnClickListener {
                val notebook = notebooks[adapterPosition]
                onClick(notebook)
            }

            btnDelete.setOnClickListener {
                val notebook = notebooks[adapterPosition]
                onDelete(notebook, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notebook, parent, false)
        return NotebookViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        val notebook = notebooks[position]
        holder.titleText.text = notebook.title
    }

    override fun getItemCount(): Int = notebooks.size

    fun removeAt(position: Int) {
        notebooks.removeAt(position)
        notifyItemRemoved(position)
    }
}

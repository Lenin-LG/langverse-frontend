package com.example.proyfronted.activitys

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Notes.Client.RetrofitClient
import com.example.proyfronted.backend.Notes.dto.NoteDto
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Notes.NoteAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesActivity : AppCompatActivity() {

    private lateinit var recyclerNotes: RecyclerView
    private lateinit var btnAddNote: Button
    private lateinit var btnBackToNotebooks: Button
    private lateinit var txtNotebookTitle: TextView
    private lateinit var noteAdapter: NoteAdapter
    private val notesList = mutableListOf<NoteDto>()

    private var notebookId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        notebookId = intent.getLongExtra("NOTEBOOK_ID", -1L)
        val notebookTitle = intent.getStringExtra("NOTEBOOK_TITLE") ?: ""

        if (notebookId == -1L) {
            Toast.makeText(this, "Notebook no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerNotes = findViewById(R.id.recyclerNotes)
        btnAddNote = findViewById(R.id.btnAddNote)
        btnBackToNotebooks = findViewById(R.id.btnBackToNotebooks)
        txtNotebookTitle = findViewById(R.id.txtNotebookTitle)
        txtNotebookTitle.text = notebookTitle

        noteAdapter = NoteAdapter(
            notesList,
            onDelete = { note, position ->
                note.id?.let { id -> confirmDeleteNote(id, position) }
            },
            onEdit = { note ->
                showEditNoteDialog(note)
            }
        )


        recyclerNotes.layoutManager = LinearLayoutManager(this)
        recyclerNotes.adapter = noteAdapter

        btnAddNote.setOnClickListener { showCreateNoteDialog() }
        btnBackToNotebooks.setOnClickListener { finish() }

        loadNotes()
    }

    private fun loadNotes() {
        CoroutineScope(Dispatchers.IO).launch {
                val notes = RetrofitClient.noteApi.getNotesByNotebook(notebookId)
                withContext(Dispatchers.Main) {
                    notesList.clear()
                    notesList.addAll(notes)
                    noteAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun showCreateNoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTitle)
        val contentEdit = dialogView.findViewById<EditText>(R.id.editContent)

        AlertDialog.Builder(this)
            .setTitle("Nueva Nota")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val title = titleEdit.text.toString()
                val content = contentEdit.text.toString()
                createNote(title, content)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createNote(title: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
                val note = NoteDto(
                    title = title,
                    content = content,
                    notebookId = notebookId
                )
                val created = RetrofitClient.noteApi.createNote(note)

                withContext(Dispatchers.Main) {
                    notesList.add(0, created)
                    noteAdapter.notifyItemInserted(0)
                    recyclerNotes.scrollToPosition(0)
                }
        }
    }

    private fun confirmDeleteNote(id: Long, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar nota")
            .setMessage("¿Estás seguro de eliminar esta nota?")
            .setPositiveButton("Sí") { _, _ ->
                deleteNote(id, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteNote(id: Long, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.noteApi.deleteNote(id)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        notesList.removeAt(position)
                        noteAdapter.notifyItemRemoved(position)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NotesActivity, "Error al eliminar nota", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun showEditNoteDialog(note: NoteDto) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTitle)
        val contentEdit = dialogView.findViewById<EditText>(R.id.editContent)

        titleEdit.setText(note.title)
        contentEdit.setText(note.content)

        AlertDialog.Builder(this)
            .setTitle("Editar Nota")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val updatedNote = note.copy(
                    title = titleEdit.text.toString(),
                    content = contentEdit.text.toString()
                )
                updateNote(updatedNote)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun updateNote(note: NoteDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updated = RetrofitClient.noteApi.updateNote(note.id!!, note)

                withContext(Dispatchers.Main) {
                    val index = notesList.indexOfFirst { it.id == note.id }
                    if (index != -1) {
                        notesList[index] = updated
                        noteAdapter.notifyItemChanged(index)
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@NotesActivity, "Error al actualizar nota", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}

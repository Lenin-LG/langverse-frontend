package com.example.proyfronted.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Speaking.Client.RetrofitClient
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Speaking.PhraseAdapter
import kotlinx.coroutines.launch

class PhraseActivity : AppCompatActivity() {

    private lateinit var recyclerPhrases: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phrases)

        val topicId = intent.getLongExtra("topicId", -1)
        val topicName = intent.getStringExtra("topicName") ?: ""

        findViewById<TextView>(R.id.txtTopicTitle).text = topicName
        recyclerPhrases = findViewById(R.id.recyclerPhrases)
        recyclerPhrases.layoutManager = LinearLayoutManager(this)

        loadPhrases(topicId)
        val btnBackToTopics = findViewById<Button>(R.id.btnBackToTopics)
        btnBackToTopics.setOnClickListener {
            finish()
        }
    }

    private fun loadPhrases(topicId: Long) {
        lifecycleScope.launch {
            try {
                val phrases = RetrofitClient.phraseApi.getPhrasesByTopic(topicId)
                val adapter = PhraseAdapter(phrases) { selectedPhrase ->
                    val intent = Intent(this@PhraseActivity, SpeakingActivity::class.java)
                    intent.putExtra("phraseId", selectedPhrase.phraseId)
                    intent.putExtra("phraseText", selectedPhrase.text)
                    startActivity(intent)
                }
                recyclerPhrases.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@PhraseActivity, "Error cargando frases", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.example.proyfronted.activitys
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyfronted.backend.Speaking.Client.RetrofitClient
import com.example.proyfronted.HomeActivity
import com.example.proyfronted.R
import com.example.proyfronted.ui.adapters.Speaking.TopicAdapter
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
class TopicActivity : AppCompatActivity() {

    private lateinit var recyclerTopics: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        recyclerTopics = findViewById(R.id.recyclerTopics)
        recyclerTopics.layoutManager = LinearLayoutManager(this)

        loadTopics()
        val btnBackToHome = findViewById<Button>(R.id.btnBackToHome)
        btnBackToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


    }

    private fun loadTopics() {
        lifecycleScope.launch {
            try {
                val topics = RetrofitClient.topicApi.getAllTopics()
                val adapter = TopicAdapter(topics) { selectedTopic ->
                    val intent = Intent(this@TopicActivity, PhraseActivity::class.java)
                    intent.putExtra("topicId", selectedTopic.topicId)
                    intent.putExtra("topicName", selectedTopic.name)
                    startActivity(intent)
                }
                recyclerTopics.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@TopicActivity, "Error cargando temas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

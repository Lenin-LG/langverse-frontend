package com.example.proyfronted.activitys

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.backend.Speaking.Client.RetrofitClient
import com.example.proyfronted.backend.Speaking.dto.SpeakingPracticeDto
import com.example.proyfronted.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
class SpeakingActivity : AppCompatActivity() {

    private lateinit var txtTargetPhrase: TextView
    private lateinit var txtSpokenText: TextView
    private lateinit var txtAccuracy: TextView
    private lateinit var btnSpeak: Button

    private var targetPhrase: String = ""
    private var selectedPhraseId: Long = -1L

    private val speechToTextLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val resultText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""

            txtSpokenText.text = resultText

            val accuracy = calculateSimilarity(targetPhrase, resultText)
            txtAccuracy.text = "Precisión: ${"%.1f".format(accuracy * 100)}%"

            savePractice(resultText, accuracy)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speaking)

        txtTargetPhrase = findViewById(R.id.txtTargetPhrase)
        txtSpokenText = findViewById(R.id.txtSpokenText)
        txtAccuracy = findViewById(R.id.txtAccuracy)
        btnSpeak = findViewById(R.id.btnSpeak)

        targetPhrase = intent.getStringExtra("phraseText") ?: "Frase no recibida"
        selectedPhraseId = intent.getLongExtra("phraseId", -1)

        txtTargetPhrase.text = targetPhrase

        btnSpeak.setOnClickListener {
            startSpeechToText()
        }

        findViewById<Button>(R.id.btnBackToPhrases).setOnClickListener {
            finish()
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...")
        }

        try {
            speechToTextLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateSimilarity(original: String, spoken: String): Double {
        val cleanedOriginal = original.lowercase(Locale.ROOT).trim()
        val cleanedSpoken = spoken.lowercase(Locale.ROOT).trim()

        val levenshteinScore = 1.0 - levenshtein(cleanedOriginal, cleanedSpoken).toDouble() /
                maxOf(cleanedOriginal.length, cleanedSpoken.length).coerceAtLeast(1)

        val wordMatchScore = calculateWordMatch(cleanedOriginal, cleanedSpoken)

        return (levenshteinScore * 0.4 + wordMatchScore * 0.6)
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in a.indices) dp[i + 1][0] = i + 1
        for (j in b.indices) dp[0][j + 1] = j + 1
        for (i in a.indices) {
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                dp[i + 1][j + 1] = minOf(dp[i][j + 1] + 1, dp[i + 1][j] + 1, dp[i][j] + cost)
            }
        }
        return dp[a.length][b.length]
    }

    private fun calculateWordMatch(original: String, spoken: String): Double {
        val originalWords = original.split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()
        val spokenWords = spoken.split("\\s+".toRegex()).filter { it.isNotBlank() }

        if (originalWords.isEmpty()) return 0.0

        val matched = spokenWords.count { it in originalWords }

        return matched.toDouble() / originalWords.size
    }

    private fun savePractice(spoken: String, accuracy: Double) {
        val dto = SpeakingPracticeDto(
            phraseId = selectedPhraseId,
            spokenText = spoken,
            accuracy = accuracy
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.speakingPracticeApi.savePractice(dto)
            } catch (_: Exception) {}
        }
    }
}

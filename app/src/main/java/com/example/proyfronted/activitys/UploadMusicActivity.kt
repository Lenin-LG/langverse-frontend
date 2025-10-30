package com.example.proyfronted.activitys
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.R
import com.example.proyfronted.backend.Music.Client.RetrofitClient
import com.example.proyfronted.backend.Music.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadMusicActivity : AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDuration: EditText
    private lateinit var btnSelectAudio: Button
    private lateinit var btnSelectLyricsEs: Button
    private lateinit var btnSelectLyricsEn: Button
    private lateinit var btnUploadSong: Button
    private lateinit var btnBack: ImageButton

    private lateinit var txtAudioSelected: TextView
    private lateinit var txtLyricsEsSelected: TextView
    private lateinit var txtLyricsEnSelected: TextView

    private var audioUri: Uri? = null
    private var lyricsEsUri: Uri? = null
    private var lyricsEnUri: Uri? = null

    private val REQUEST_AUDIO = 1
    private val REQUEST_LYRICS_ES = 2
    private val REQUEST_LYRICS_EN = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_music)

        // Inicializar vistas
        btnBack = findViewById(R.id.btnBack)
        edtTitle = findViewById(R.id.edtSongTitle)
        edtDuration = findViewById(R.id.edtDuration)
        btnSelectAudio = findViewById(R.id.btnSelectAudio)
        btnSelectLyricsEs = findViewById(R.id.btnSelectLyricsEs)
        btnSelectLyricsEn = findViewById(R.id.btnSelectLyricsEn)
        btnUploadSong = findViewById(R.id.btnUploadSong)

        txtAudioSelected = findViewById(R.id.txtAudioSelected)
        txtLyricsEsSelected = findViewById(R.id.txtLyricsEsSelected)
        txtLyricsEnSelected = findViewById(R.id.txtLyricsEnSelected)

        // Acciones
        btnBack.setOnClickListener { finish() }
        btnSelectAudio.setOnClickListener { selectFile(REQUEST_AUDIO, "audio/*") }
        btnSelectLyricsEs.setOnClickListener { selectFile(REQUEST_LYRICS_ES, "*/*") }
        btnSelectLyricsEn.setOnClickListener { selectFile(REQUEST_LYRICS_EN, "*/*") }
        btnUploadSong.setOnClickListener { uploadMusic() }
    }

    private fun selectFile(requestCode: Int, type: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = type
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            when (requestCode) {
                REQUEST_AUDIO -> {
                    audioUri = uri
                    txtAudioSelected.text = uri?.lastPathSegment ?: "Seleccionado"
                }
                REQUEST_LYRICS_ES -> {
                    lyricsEsUri = uri
                    txtLyricsEsSelected.text = uri?.lastPathSegment ?: "Seleccionado"
                }
                REQUEST_LYRICS_EN -> {
                    lyricsEnUri = uri
                    txtLyricsEnSelected.text = uri?.lastPathSegment ?: "Seleccionado"
                }
            }
        }
    }

    private fun uploadMusic() {
        val title = edtTitle.text.toString().trim()
        val durationStr = edtDuration.text.toString().trim()

        if (title.isEmpty() || durationStr.isEmpty() || audioUri == null) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val durationInt = durationStr.toIntOrNull()
        if (durationInt == null) {
            Toast.makeText(this, "Duración inválida", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val audioPart = uriToPart("audioFile", audioUri!!)
                val lyricsEsPart = lyricsEsUri?.let { uriToPart("lyricsEs", it) }
                val lyricsEnPart = lyricsEnUri?.let { uriToPart("lyricsEn", it) }

                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val durationPart = durationInt.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.musicApi.uploadSong(
                    title = titlePart,
                    duration = durationPart,
                    audioFile = audioPart,
                    lyricsEs = lyricsEsPart,
                    lyricsEn = lyricsEnPart
                )

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UploadMusicActivity, "✅ Canción subida correctamente", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@UploadMusicActivity, "❌ Error al subir canción", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@UploadMusicActivity, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun uriToPart(name: String, uri: Uri): MultipartBody.Part {
        val file = FileUtil.from(this, uri)
        val mediaType = contentResolver.getType(uri)?.toMediaTypeOrNull()
        val requestFile = file.asRequestBody(mediaType)
        return MultipartBody.Part.createFormData(name, file.name, requestFile)
    }
}
package com.example.proyfronted.activitys

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.backend.Movie.MovieDto
import com.example.proyfronted.backend.Movie.RetrofitClient
import com.example.proyfronted.R
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink

class UploadMovieActivity : AppCompatActivity() {

    private var selectedUris = mutableMapOf<String, Uri?>()

    private lateinit var radioMovie: MaterialRadioButton
    private lateinit var radioSeries: MaterialRadioButton
    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtDuration: EditText
    private lateinit var edtReleaseDate: EditText
    private lateinit var edtSeason: EditText
    private lateinit var edtEpisode: EditText
    private lateinit var layoutSeriesFields: LinearLayout
    private lateinit var btnUpload: Button
    private lateinit var btnBack : ImageButton
    private val filePickers = mutableMapOf<String, ActivityResultLauncher<Intent>>()

    private val fileRequestMap = mapOf(
        "file" to Pair(R.id.btnSelectVideo, R.id.txtVideoSelected),
        "audioEs" to Pair(R.id.btnSelectAudioEs, R.id.txtAudioEsSelected),
        "audioEn" to Pair(R.id.btnSelectAudioEn, R.id.txtAudioEnSelected),
        "subsEs" to Pair(R.id.btnSelectSubsEs, R.id.txtSubsEsSelected),
        "subsEn" to Pair(R.id.btnSelectSubsEn, R.id.txtSubsEnSelected),
        "imageBanner" to Pair(R.id.btnSelectImage, R.id.txtImageSelected)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_movie)
        btnBack = findViewById(R.id.btnBack)
        radioMovie = findViewById(R.id.radioMovie)
        radioSeries = findViewById(R.id.radioSeries)
        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        edtDuration = findViewById(R.id.edtDuration)
        edtReleaseDate = findViewById(R.id.edtReleaseDate)
        edtSeason = findViewById(R.id.edtSeason)
        edtEpisode = findViewById(R.id.edtEpisode)
        layoutSeriesFields = findViewById(R.id.seriesFields)
        btnUpload = findViewById(R.id.btnUploadMovie)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupCategory)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            layoutSeriesFields.visibility = if (checkedId == R.id.radioSeries) View.VISIBLE else View.GONE
        }
        btnBack.setOnClickListener {
            finish()
        }
        fileRequestMap.forEach { (key, pair) ->
            val (buttonId, textViewId) = pair

            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri = result.data!!.data
                    if (uri != null) {
                        selectedUris[key] = uri
                        findViewById<TextView>(textViewId)?.text = uri.lastPathSegment
                    }
                }
            }
            filePickers[key] = launcher

            findViewById<Button>(buttonId).setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
                filePickers[key]?.launch(Intent.createChooser(intent, "Selecciona archivo"))
            }
        }

        btnUpload.setOnClickListener { subirContenido() }
    }

    private fun getFileName(uri: Uri): String {
        var name = "file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    private fun createRequestBodyFromUri(uri: Uri): RequestBody {
        return object : RequestBody() {
            override fun contentType() = "application/octet-stream".toMediaTypeOrNull()
            override fun contentLength(): Long {
                return contentResolver.openAssetFileDescriptor(uri, "r")?.length ?: -1
            }
            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { input ->
                    input.copyTo(sink.outputStream())
                }
            }
        }
    }

    private fun createEmptyPart(name: String): MultipartBody.Part {
        val emptyRequestBody = ByteArray(0).toRequestBody("application/octet-stream".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, "", emptyRequestBody)
    }

    private fun subirContenido() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movieDto = MovieDto(
                    title = edtTitle.text.toString(),
                    description = edtDescription.text.toString(),
                    idCategory = if (radioSeries.isChecked) 2 else 1,
                    durationInMinutes = edtDuration.text.toString().toIntOrNull() ?: 0,
                    releaseDate = edtReleaseDate.text.toString(),
                    seasonNumber = if (radioSeries.isChecked) edtSeason.text.toString().toIntOrNull() else null,
                    episodeNumber = if (radioSeries.isChecked) edtEpisode.text.toString().toIntOrNull() else null
                )

                val gson = Gson()
                val jsonBody = gson.toJson(movieDto).toRequestBody("application/json".toMediaTypeOrNull())

                if (selectedUris["file"] == null) {
                    runOnUiThread {
                        Toast.makeText(this@UploadMovieActivity, "Debes seleccionar el archivo de video", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val files = mutableMapOf<String, MultipartBody.Part>()
                selectedUris.forEach { (key, uri) ->
                    uri?.let {
                        val requestBody = createRequestBodyFromUri(uri)
                        val fileName = getFileName(uri)
                        files[key] = MultipartBody.Part.createFormData(key, fileName, requestBody)
                    }
                }

                val response = RetrofitClient.api.uploadMovie(
                    movieJson = MultipartBody.Part.createFormData("movie", null, jsonBody),
                    file = files["file"]!!,
                    audioEn = files["audioEn"] ?: createEmptyPart("audioEn"),
                    audioEs = files["audioEs"] ?: createEmptyPart("audioEs"),
                    subsEn = files["subsEn"] ?: createEmptyPart("subsEn"),
                    subsEs = files["subsEs"] ?: createEmptyPart("subsEs"),
                    image = files["imageBanner"] ?: createEmptyPart("imageBanner")
                )

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UploadMovieActivity, "✅ Subido correctamente", Toast.LENGTH_LONG).show()
                    } else {
                        val errorText = response.errorBody()?.string() ?: "Error desconocido"
                        AlertDialog.Builder(this@UploadMovieActivity)
                            .setTitle("❌ Error al subir")
                            .setMessage("Código: ${response.code()}\n$errorText")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    AlertDialog.Builder(this@UploadMovieActivity)
                        .setTitle("❌ Excepción")
                        .setMessage(e.localizedMessage ?: "Error desconocido")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }
}

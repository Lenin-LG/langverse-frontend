package com.example.proyfronted.activitys


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.R
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.util.Log
import android.widget.ImageButton
import androidx.core.content.FileProvider
import com.example.proyfronted.backend.report.client.RetrofitReportClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AdminActivity : AppCompatActivity() {

    private lateinit var btnUserStats: Button
    private lateinit var btnManageMovies: MaterialButton
    private lateinit var btnManageMusic: MaterialButton
    private lateinit var btnBack : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrator)

        btnUserStats = findViewById(R.id.btnViewUserStats)
        btnManageMovies = findViewById(R.id.btnManageMovies)
        btnManageMusic = findViewById(R.id.btnManageMusic)
        btnBack = findViewById(R.id.btnBack)
        btnUserStats.setOnClickListener {
            descargarReporteUsuarios()
        }
        btnBack.setOnClickListener {
            finish()
        }
        btnManageMovies.setOnClickListener {
            val intent = Intent(this, UploadMovieActivity::class.java)
            startActivity(intent)
        }
        btnManageMusic.setOnClickListener {
            val intent = Intent(this, UploadMusicActivity::class.java)
            startActivity(intent)
        }
    }


    private fun descargarReporteUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ResponseBody> =
                    RetrofitReportClient.reportApi.downloadUserReport()

                if (response.isSuccessful && response.body() != null) {
                    val inputStream = response.body()!!.byteStream()
                    val file = File(getExternalFilesDir(null), "users.pdf")
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    outputStream.close()
                    inputStream.close()

                    abrirPDF(file)
                } else {
                    mostrarError("Error downloading PDF (${response.code()})")
                }
            } catch (e: Exception) {
                mostrarError("Error: ${e.message}")
            }
        }
    }

    private fun abrirPDF(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        runOnUiThread {
                startActivity(intent)
        }
    }

    private fun mostrarError(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
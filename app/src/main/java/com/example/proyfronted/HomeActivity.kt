package com.example.proyfronted

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.activitys.AdminActivity
import com.example.proyfronted.activitys.MovieActivity
import com.example.proyfronted.activitys.MusicActivity
import com.example.proyfronted.activitys.ProfileActivity
import com.example.proyfronted.activitys.TopicActivity
import com.example.proyfronted.backend.Auth.utils.refreshAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.example.proyfronted.backend.Auth.SessionManager
import com.example.proyfronted.activitys.AuthActivity
import com.example.proyfronted.activitys.NotebookActivity
import org.json.JSONObject
import android.util.Base64
import com.google.android.material.card.MaterialCardView
import kotlin.io.encoding.ExperimentalEncodingApi


@androidx.media3.common.util.UnstableApi
class HomeActivity : AppCompatActivity() {
    private var tokenRefreshJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btnMusic).setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnMovies).setOnClickListener {
            val intent = Intent(this, MovieActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnNotes).setOnClickListener {
            val intent = Intent(this, NotebookActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnSpeaking).setOnClickListener {
            val intent = Intent(this, TopicActivity::class.java)
            startActivity(intent)
        }
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            SessionManager(this).clear() // Limpia tokens
            tokenRefreshJob?.cancel()    // Cancela job de refresh
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        val cardAdmin = findViewById<MaterialCardView>(R.id.cardAdmin)
        val btnAdmin = findViewById<Button>(R.id.btnAdministrator)
        val token = SessionManager(this).fetchAccessToken()

        if (tieneRolAdmin(token)) {
            cardAdmin.visibility = View.VISIBLE
            btnAdmin.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        }
        startTokenRefreshJob()
    }
    private fun startTokenRefreshJob() {
        tokenRefreshJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(4 * 60 * 1000) // 4 minutos
                refreshAccessToken(applicationContext)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenRefreshJob?.cancel()
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun tieneRolAdmin(jwt: String?): Boolean {
        if (jwt == null) return false
        return try {
            val parts = jwt.split(".")
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            val resourceAccess = json.getJSONObject("resource_access")
            val clientRoles = resourceAccess.getJSONObject("microservice-client")
            val rolesArray = clientRoles.getJSONArray("roles")
            (0 until rolesArray.length()).any { rolesArray.getString(it) == "admin_client_role" }
        } catch (e: Exception) {
            false
        }
    }

}
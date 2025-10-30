package com.example.proyfronted.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyfronted.R
import com.example.proyfronted.backend.Auth.Client.RetrofitClient
import com.example.proyfronted.backend.Auth.SessionManager
import com.example.proyfronted.backend.Auth.utils.refreshAccessToken
import com.example.proyfronted.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserInfo()

        binding.btnDeleteAccount.setOnClickListener { deleteAccount() }
        binding.btnDisableAccount.setOnClickListener { disableAccount() }
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun getUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAccessToken(applicationContext)
            val token = SessionManager(this@ProfileActivity).getToken()
            val response = RetrofitClient.api.getUserInfo("Bearer $token")

            if (response.isSuccessful) {
                val user = response.body()
                runOnUiThread {
                    binding.tvUsername.text = user?.username
                    binding.tvEmail.text = user?.email
                }
            }
        }
    }

    private fun deleteAccount() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = SessionManager(this@ProfileActivity).getToken()
            val response = RetrofitClient.api.deleteUser("Bearer $token")

            if (response.isSuccessful) {
                SessionManager(this@ProfileActivity).clear()
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProfileActivity, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Error al eliminar cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun disableAccount() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = SessionManager(this@ProfileActivity).getToken()
            val response = RetrofitClient.api.setUserEnabled("Bearer $token", mapOf("enabled" to false))

            if (response.isSuccessful) {
                SessionManager(this@ProfileActivity).clear()
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Cuenta desactivada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProfileActivity, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Error al desactivar cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

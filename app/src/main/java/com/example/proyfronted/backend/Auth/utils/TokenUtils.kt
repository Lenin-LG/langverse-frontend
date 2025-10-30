package com.example.proyfronted.backend.Auth.utils
import android.content.Context
import android.content.Intent
import com.example.proyfronted.activitys.AuthActivity
import com.example.proyfronted.backend.Auth.Client.RetrofitClient
import com.example.proyfronted.backend.Auth.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun refreshAccessToken(context: Context) {
    withContext(Dispatchers.IO) {
        val session = SessionManager(context)
        val refreshToken = session.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            redirectToLogin(context)
            return@withContext
        }

        val response = RetrofitClient.api.refreshToken(mapOf("refresh_token" to refreshToken))

        if (response.isSuccessful) {
            val newAccessToken = response.body()?.access_token
            val newRefreshToken = response.body()?.refresh_token

            if (!newAccessToken.isNullOrEmpty() && !newRefreshToken.isNullOrEmpty()) {
                session.saveToken(newAccessToken)
                session.saveRefreshToken(newRefreshToken)
            } else {
                redirectToLogin(context)
            }
        } else {  redirectToLogin(context)
        }
    }
}

private fun redirectToLogin(context: Context) {
    SessionManager(context).clear()  // Limpia los tokens
    val intent = Intent(context, AuthActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}
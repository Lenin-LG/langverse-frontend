package com.example.proyfronted.backend.Auth

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }
    fun saveRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)
    fun clear() {
        prefs.edit().clear().apply()
    }
    fun fetchAccessToken(): String? {
        return prefs.getString("token", null)
    }

}

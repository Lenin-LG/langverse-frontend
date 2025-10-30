package com.example.proyfronted.backend.Movie

import retrofit2.converter.scalars.ScalarsConverterFactory
import android.content.Context
import com.example.proyfronted.backend.Auth.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
object RetrofitClient {

    private lateinit var retrofit: Retrofit
    lateinit var api: MovieApiService

    fun init(context: Context) {
        val sessionManager = SessionManager(context)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .connectTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
            .readTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
            .writeTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
            .addInterceptor(TokenInterceptor { sessionManager.fetchAccessToken() })
            .addInterceptor(logging)
            .build()


        retrofit = Retrofit.Builder()
            .baseUrl("http://3.238.94.172:8080/")
          //  .baseUrl("http://192.168.1.38:8080/")
        //   .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(MovieApiService::class.java)
    }
}
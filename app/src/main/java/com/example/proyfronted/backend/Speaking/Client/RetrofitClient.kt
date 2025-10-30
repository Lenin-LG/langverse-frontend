package com.example.proyfronted.backend.Speaking.Client

import android.content.Context
import com.example.proyfronted.backend.Auth.SessionManager
import com.example.proyfronted.backend.Movie.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    lateinit var topicApi: TopicApi
    lateinit var phraseApi: PhraseApi
    lateinit var speakingPracticeApi: SpeakingPracticeApi
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val sessionManager = SessionManager(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor { sessionManager.fetchAccessToken() }) // agrega el token Bearer
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://3.238.94.172:8080/")
            //  .baseUrl("http://10.0.2.2:8080/") // Asegúrate que el puerto y ruta coincidan
           // .baseUrl("http://192.168.1.38:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        topicApi = retrofit.create(TopicApi::class.java)
        phraseApi = retrofit.create(PhraseApi::class.java)
        speakingPracticeApi = retrofit.create(SpeakingPracticeApi::class.java)
    }
}
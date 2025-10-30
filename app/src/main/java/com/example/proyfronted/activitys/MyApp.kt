package com.example.proyfronted.activitys

import android.app.Application
import com.example.proyfronted.backend.Notes.Client.RetrofitClient
import com.example.proyfronted.backend.Movie.RetrofitClient as MovieClient
import com.example.proyfronted.backend.Music.Client.RetrofitClient as MusicClient
import com.example.proyfronted.backend.Speaking.Client.RetrofitClient as SpeakingClient
import com.example.proyfronted.backend.report.client.RetrofitReportClient as ReportClient
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        RetrofitClient.init(applicationContext)
        MovieClient.init(applicationContext)
        MusicClient.init(applicationContext)
        SpeakingClient.init(applicationContext)
        ReportClient.init(applicationContext)
    }
}

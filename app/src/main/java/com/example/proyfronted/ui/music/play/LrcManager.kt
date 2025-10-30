package com.example.proyfronted.ui.music.play

import android.os.Handler
import android.os.Looper
import com.example.proyfronted.ui.music.play.PlayFragment.LrcLine
import okhttp3.*
import java.io.IOException

class LrcManager {

    fun downloadLrc(url: String?, callback: (List<LrcLine>) -> Unit) {
        if (url.isNullOrEmpty()) return

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return

                val lrcText = response.body?.string() ?: ""
                val lines = parseLrc(lrcText)

                // Aseguramos que el callback se ejecute en el hilo principal
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    callback(lines)
                }
            }
        })
    }

    private fun parseLrc(lrcText: String): List<LrcLine> {
        val lines = mutableListOf<LrcLine>()
        val regex = Regex("""\[(\d+):(\d+)\.(\d+)](.*)""")

        lrcText.lines().forEach { line ->
            val match = regex.find(line)
            if (match != null) {
                val (min, sec, ms, text) = match.destructured
                val timeMillis = (min.toLong() * 60 + sec.toLong()) * 1000 + ms.toLong() * 10
                lines.add(LrcLine(timeMillis, text.trim()))
            }
        }

        return lines.sortedBy { it.timeMillis }
    }

}

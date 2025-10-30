package com.example.proyfronted.backend.Music

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtil {
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, System.currentTimeMillis().toString())
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }
}

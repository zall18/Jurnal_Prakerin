package com.example.latihan1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun getImageFromUrl(connectionString: String): Bitmap? {
    return withContext(Dispatchers.IO){
        try {
            val url = URL(connectionString)
            val connection = url.openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.requestMethod = "GET"
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}
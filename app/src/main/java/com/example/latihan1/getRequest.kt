package com.example.latihan1

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

suspend fun getRequest(connectionString: String, token: String?): Result<String> {
    return withContext(Dispatchers.IO){
        try {
            var url = URL(connectionString)
            var redirect = false
            var response = StringBuilder()

            do {
                var connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                if(token != null){
                    connection.setRequestProperty("Authorization", "application/json")
                }
                connection.connect()

                var responseCode = connection.responseCode

                if(responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP){
                    var newUrl = connection.getHeaderField("Location")
                    url = URL(newUrl)
                    redirect = true

                }else{
                    redirect = false

                    var inputStream = if(responseCode in 200 .. 299){
                        connection.inputStream
                    }else{
                        connection.errorStream
                    }

                    var line: String?
                    var reader = BufferedReader(InputStreamReader(inputStream))
                    while (reader.readLine().also { line = it } != null){
                        response.append(line)
                    }

                    if(responseCode !in 200 .. 299){
                        return@withContext Result.failure(Exception("Error with response code $responseCode \n $response"))
                    }
                }
                connection.disconnect()
            }while (redirect)
            Result.success(response.toString())
        }catch (e: Exception){
            Result.failure(e)

        }
    }
}
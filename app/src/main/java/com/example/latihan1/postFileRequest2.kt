package com.example.latihan1

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

@RequiresApi(Build.VERSION_CODES.O)
suspend fun postFileRequest2(connectionString: String, file: File?, dataName: MutableList<String>, dataValue : MutableList<String>, filename : String, token: String?): Result<String> {
    return withContext(Dispatchers.IO){
        try {
            var url = URL(connectionString)
            var redirect = false
            var response = StringBuilder()
            val boundary = "Boundary-" + System.currentTimeMillis()
            val LINE_FEED = "\r\n"
            val maxBufferedReader = 1080 * 1080

            do {
                var connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                connection.setRequestProperty("Accept", "application/json")
                if(token != null){
                    connection.setRequestProperty("Authorization", "Bearer $token")
                }
                connection.doOutput = true

                var dataOutputStream = connection.outputStream
                var writer = PrintWriter(OutputStreamWriter(dataOutputStream, "UTF-8"), true)

                for (i in 0 until dataName.size)
                {

                    writer.append("--").append(boundary).append(LINE_FEED)
                    writer.append("Content-Disposition: form-data; name = \"").append(dataName[i]).append("\"").append(LINE_FEED)
                    writer.append(LINE_FEED)
                    writer.append(dataValue[i])
                    writer.flush()

                }

                writer.append("--").append(boundary).append(LINE_FEED)
                writer.append("Content-Disposition: form-data; name = \"").append(filename).append("\"; filename = \"").append(file?.name.toString()).append(LINE_FEED)
                writer.append(LINE_FEED)
                writer.append("Content-Type = ").append(Files.probeContentType(file?.toPath()))
                writer.flush()

                var inputStream = FileInputStream(file)
                inputStream.copyTo(dataOutputStream, maxBufferedReader)

                inputStream.close()
                dataOutputStream.flush()
                writer.append(LINE_FEED)
                writer.flush()

                writer.append(LINE_FEED).flush()
                writer.append("--").append(boundary).append("--").append(LINE_FEED)
                writer.close()

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
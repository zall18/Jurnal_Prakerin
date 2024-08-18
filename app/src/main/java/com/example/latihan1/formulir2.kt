package com.example.latihan1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class formulir2 : AppCompatActivity() {
    lateinit var session: SharedPreferences
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulir2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var tanggal: EditText = findViewById(R.id.tanggal_input)
        var status: EditText = findViewById(R.id.status_input)
        var catatan: EditText = findViewById(R.id.catatan_input)
        var kirim: AppCompatButton = findViewById(R.id.kirim_button)
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var connection = Connection()
        var file: File? = null


        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val selectedImageUri = data.data ?: return@registerForActivityResult
                try{
                    file = File(getRealPathFromURI(selectedImageUri))
                    Log.d("file", "onCreate: $file")

                }catch (e: Exception){
                    println(e)
                    Toast.makeText(applicationContext, "failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        var image_button: AppCompatButton = findViewById(R.id.image_button)
        image_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        kirim.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            val current = formatter.format(date)

            var jsonObject = JSONObject().apply {
                put("tanggal", current.toString())
                put("status", status.text.toString())
                put("catatan", catatan.text.toString())
                put("id_siswa", session.getString("idsiswa", ""))
                put("bukti", file)
            }

            var dataName = mutableListOf<String>().apply {
                add("tanggal")
                add("status")
                add("catatan")
                add("id_siswa")

            }

            var dataValue = mutableListOf<String>().apply {
                add(current.toString())
                add(status.text.toString())
                add(catatan.text.toString())
                add(session.getString("idsiswa", "").toString())
            }

            lifecycleScope.launch {
                var result = postfileRequest(connection.connection + "formulir/add?token=" + session.getString("token", ""), file, "bukti", dataName, dataValue, null)

                result.fold(
                    onSuccess = {
                            response -> var jsonObject2 = JSONObject(response)
                        Log.d("formulir", "onCreate: $jsonObject2")
                        if(jsonObject2.getBoolean("success")){
                            startActivity(Intent(applicationContext, bottomNav::class.java))
                        }
                    },
                    onFailure = {
                            error -> error.printStackTrace()
                    }
                )

            }
        }

        var back: ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }
    }

    fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            return filePath
        }
        return null
    }


}
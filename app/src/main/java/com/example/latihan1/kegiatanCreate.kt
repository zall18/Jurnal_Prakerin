package com.example.latihan1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
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

class kegiatanCreate : AppCompatActivity() {

    lateinit var session: SharedPreferences
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kegiatan_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var connection =Connection()
        var file: File? = null
        /*pickImageLauncher = registerForActivityResult(
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
        }*/
        
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            result ->
            if (result.resultCode == Activity.RESULT_OK)
            {
                val data = result.data?: return@registerForActivityResult
                val selectedImageUri = data.data?: return@registerForActivityResult

                try {
                    file = File(getRealPathFromURI(selectedImageUri))

                }catch (e: Exception)
                {
                    println(e)
                }
            }
        }

        var back: ImageView = findViewById(R.id.back_kegiatan)
        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }

        var desk: EditText = findViewById(R.id.deskripsi_input)
        var durasi: EditText = findViewById(R.id.durasi_input)

        var image: AppCompatButton = findViewById(R.id.image_button_kegiatan)
        image.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        var kirim: AppCompatButton = findViewById(R.id.kirim_button_kegiatan)
        kirim.setOnClickListener {
            var jsonObject = JSONObject().apply {
                put("deskripsi", desk.text)
                put("durasi", durasi.text)
                put("id_absensi", "100")
                put("id_siswa", session.getString("idsiswa", ""))
                put("id_kelas", session.getString("id_kelas", ""))
                put("foto", file)
            }

            var dataName = mutableListOf<String>().apply {
                add("deskripsi")
                add("durasi")
                add("id_absensi")
                add("id_siswa")
                add("id_kelas")
            }

            var dataValue = mutableListOf<String>().apply {
                add(desk.text.toString())
                add(durasi.text.toString())
                add(session.getString("idsiswa", "").toString())
                add(session.getString("idsiswa", "").toString())
                add(session.getString("idkelas", "").toString())
            }

            lifecycleScope.launch {
                var result = postfileRequest(connection.connection + "kegiatan/add?token=" + session.getString("token", ""), file, "foto", dataName, dataValue, null)

                result.fold(
                    onSuccess = {
                            response -> var jsonObject2 = JSONObject(response)

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

    }
    fun getRealPathFromURI(uri: Uri): String? {
        /*val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            return filePath
        }
        return null*/
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null)
        {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filepath = cursor.getString(columnIndex)
            cursor.close()
            return filepath
        }
        return null
    }

}
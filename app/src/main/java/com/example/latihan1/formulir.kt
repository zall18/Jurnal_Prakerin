package com.example.latihan1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

class formulir: AppCompatActivity() {

    lateinit var session: SharedPreferences
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fomulir)
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
        var file: File = File("", null)

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data ?: return@registerForActivityResult
                val selectedImageUri = data.data ?: return@registerForActivityResult


                try {
                    val filePath = getPathFromUri(applicationContext, selectedImageUri)
                    file = File(filePath)
                }catch (e: Exception){
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        var image_button: AppCompatButton = findViewById(R.id.image_button)
        image_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivity(intent)
        }

        kirim.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            val current = formatter.format(date)

            var jsonObject = JSONObject().apply {
                put("tanggal", current.toString())
                put("status", status.text.toString())
                put("catatan", catatan.text.toString())
                put("id_siswa", session.getString("id", ""))
                put("bukti", "-")
            }

            lifecycleScope.launch {
               /* var result = postfileRequest(connection.connection + "formulir/add?token=" + session.getString("token", ""), file, jsonObject, null)

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
                )*/

            }
        }

        var back: ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }


    }
    fun getPathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
        if (cursor!= null) {
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(0)
            }
            cursor.close()
        }
        return filePath
    }
}

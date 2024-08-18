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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class editProfil : AppCompatActivity() {

    lateinit var session: SharedPreferences
    lateinit var imageLauncer : ActivityResultLauncher<Intent>
    var file: File? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var idsiswa = session.getString("idsiswa", "")
        var id = session.getString("id", "")
        var token = session.getString("token", "")
        var connection = Connection()

        var name: EditText = findViewById(R.id.name_edit)
        var email: EditText = findViewById(R.id.email_edit)
        var telpon: EditText = findViewById(R.id.telepon_edit)
        var alamat: EditText = findViewById(R.id.alamat_edit)
        var image: ShapeableImageView = findViewById(R.id.image_siswa_edit)


        lifecycleScope.launch {

            var result =
                getRequest(connection.connection + "auth/show/$id?token=$token", null)

            result.fold(
                onSuccess = { response ->
                    var jsonObject = JSONObject(response)

                    if (jsonObject.getBoolean("success")) {
                        var jsonObject2 = JSONObject(jsonObject.getString("user"))
                        name.setText(jsonObject2.getString("username"))
                        email.setText(jsonObject2.getString("email"))
                        telpon.setText(jsonObject2.getString("telp"))
                        alamat.setText(jsonObject2.getString("alamat"))

                        MainScope().launch {
                            var bitmap = getImageFromUrl(jsonObject2.getString("foto"))
                            image.setImageBitmap(bitmap)
                        }

                    }
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }

        imageLauncer = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            result ->
            if(result.resultCode == Activity.RESULT_OK) {
                var data = result.data ?: return@registerForActivityResult
                var imageUri = data.data ?: return@registerForActivityResult

                try {
                    file = File(getRealPathFromUri(imageUri))
                }catch (e: Exception){
                    println(e)
                }
            }
        }

        var changeImage: AppCompatButton = findViewById(R.id.edit_image_button)

        changeImage.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imageLauncer.launch(intent)
        }

        var back: ImageView = findViewById(R.id.back_profil)
        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }

        var edit: AppCompatButton = findViewById(R.id.edit_profile_button)
        edit.setOnClickListener {
            var dataName = mutableListOf<String>().apply {
                add("name")
                add("email")
                add("telp")
                add("alamat")
            }

            var dataValue = mutableListOf<String>().apply {
                add(name.text.toString())
                add(email.text.toString())
                add(telpon.text.toString())
                add(alamat.text.toString())
            }

            lifecycleScope.launch {
                var result = postfileRequest(connection.connection + "auth/editProfil/$idsiswa?token=" + session.getString("token", ""), file, "foto", dataName, dataValue, session.getString("token", ""))

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

    fun getRealPathFromUri(uri: Uri): String? {
        var projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(uri, projection, null, null, null)
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
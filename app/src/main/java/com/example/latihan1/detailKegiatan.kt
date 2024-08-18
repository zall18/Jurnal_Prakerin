package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class detailKegiatan : AppCompatActivity() {

    lateinit var session: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_kegiatan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        var connection = Connection()
        var id = session.getString("idKegiatan", "")
        var token = session.getString("token", "")
        var desk: EditText = findViewById(R.id.deskripsi_detail)
        var durasi: EditText = findViewById(R.id.durasi_detail)

        Log.d("id", "onCreate: $id")

        lifecycleScope.launch {

            var result = getRequest(connection.connection + "kegiatan/showIdKegiatan/$id?token=$token", null)

            result.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)
                    var jsonObject2 = JSONObject(jsonObject.getString("kegiatan"))
                    desk.setText(jsonObject2.getString("deskripsi"))
                    durasi.setText(jsonObject2.getString("durasi"))
                },
                onFailure = {

                }
            )

        }

        var edit: AppCompatButton = findViewById(R.id.edit_kegiatan_detail)
        edit.setOnClickListener {

            var jsonObject = JSONObject().apply {
                put("deskripsi", desk.text)
                put("durasi", durasi.text)
                put("foto", "")
            }

            lifecycleScope.launch {

                var result = postRequest(connection.connection + "kegiatan/edit/$id?token=$token", jsonObject, null)

                result.fold(
                    onSuccess = {
                            response -> var jsonObject = JSONObject(response)
                        if(jsonObject.getBoolean("success")){
                            Toast.makeText(applicationContext, "Edit berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, bottomNav::class.java))
                        }
                    },
                    onFailure = {
                        error -> error.printStackTrace()
                    }
                )

            }
        }

        var hapus: AppCompatButton = findViewById(R.id.delete_kegiatan_detail)
        hapus.setOnClickListener {
            lifecycleScope.launch {

                var result = getRequest(connection.connection + "kegiatan/delete/$id?token=$token", null)

                result.fold(
                    onSuccess = {
                            response -> var jsonObject = JSONObject(response)
                        if(jsonObject.getBoolean("success")){
                            Toast.makeText(applicationContext, "Edit berhasil", Toast.LENGTH_SHORT).show()
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
}
package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class editAkun : AppCompatActivity() {

    lateinit var session: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_akun)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var username: EditText = findViewById(R.id.username_edit)
        var password: EditText = findViewById(R.id.password_edit)
        var edit: AppCompatButton = findViewById(R.id.edit_button)
        var connection = Connection()
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var id = session.getString("id", "")
        var token = session.getString("token", "")

        lifecycleScope.launch {

            var result =
                getRequest(connection.connection + "auth/show/$id?token=$token", null)

            result.fold(
                onSuccess = { response ->
                    var jsonObject = JSONObject(response)

                    if (jsonObject.getBoolean("success")) {
                        var jsonObject2 = JSONObject(jsonObject.getString("user"))
                        username.setText(jsonObject2.getString("username"))
                    }
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }

        edit.setOnClickListener {

            var jsonObject = JSONObject().apply {
                put("username", username.text)
                put("password", password.text)
            }

            lifecycleScope.launch {
                var result = postRequest(connection.connection + "auth/edit/$id?token=$token", jsonObject, null)

                result.fold(
                    onSuccess = {
                        response -> var jsonObject2 = JSONObject(response)

                        if(jsonObject2.getBoolean("success")){
                            Toast.makeText(applicationContext, "Edit berhasil!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, bottomNav::class.java))
                            
                        }
                    },
                    onFailure = {

                    }
                )
            }

            var back: ImageView = findViewById(R.id.back_akun)
            back.setOnClickListener {
                startActivity(Intent(applicationContext, bottomNav::class.java))
            }


        }
    }
}
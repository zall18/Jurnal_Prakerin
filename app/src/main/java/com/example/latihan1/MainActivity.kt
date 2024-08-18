 package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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

 class MainActivity : AppCompatActivity() {
     lateinit var session: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username: EditText = findViewById(R.id.username)
        val password: EditText = findViewById(R.id.password)
        val login: AppCompatButton = findViewById(R.id.login_button)
        var connection = Connection()
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var edit = session.edit()

        login.setOnClickListener {
            if(username.text.length == 0){
                username.setError("This field is required")
            }else if(password.text.length == 0){
                password.setError("This field is requires")
            }else {

                var jsonObject = JSONObject().apply {
                    put("username", username.text.toString())
                    put("password", password.text.toString())
                }

                lifecycleScope.launch {
                    var result = postRequest(connection.connection + "auth/login", jsonObject, null)

                    result.fold(
                        onSuccess = {
                            response -> var jsonObject2 = JSONObject(response)

                            if(jsonObject2.getBoolean("success")){
                                var jsonObject3 = JSONObject(jsonObject2.getString("siswa"))
                                var jsonObject4 = JSONObject(jsonObject2.getString("user"))
                                edit.putString("token", jsonObject2.getString("token"))
                                edit.putString("id", jsonObject4.getString("id"))
                                edit.putString("idsiswa", jsonObject3.getString("id"))
                                edit.putString("idkelas", jsonObject3.getString("id_kelas"))
                                edit.putString("name", jsonObject3.getString("name"))
                                edit.putString("token_masuk", jsonObject4.getString("login_token"))
                                edit.putString("mode_absensi", "lokasi")
                                edit.commit()

                                Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(applicationContext, bottomNav::class.java))

                            }else{
                                Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = {
                            error -> error.printStackTrace()
                            Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_SHORT).show()

                        }
                    )
                }

            }
        }

    }
}
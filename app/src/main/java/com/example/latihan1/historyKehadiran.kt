package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class historyKehadiran : AppCompatActivity() {
    lateinit var session: SharedPreferences
    lateinit var historyAdapter: historyAdapter
    lateinit var data: MutableList<historyModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_kehadiran)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var listView = findViewById<ListView>(R.id.listview_history)
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var connection = Connection()
        data = mutableListOf<historyModel>()
        var id = session.getString("id", "")
        var token = session.getString("token", "")

        lifecycleScope.launch {

            var result = getRequest(connection.connection + "kehadiran/show/$id?token=$token", null)

            result.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)
                    var jsonArray = jsonObject.getJSONArray("kehadiran")
                    for (i in 0 until jsonArray.length()){
                        var jsonObject2 = jsonArray.getJSONObject(i)
                        data.add(historyModel(jsonObject2.getString("status"), jsonObject2.getString("tanggal")))
                    }
                    historyAdapter = historyAdapter(applicationContext, data)
                    listView.adapter = historyAdapter
                },
                onFailure = {

                }
            )

        }

        var back: ImageView = findViewById(R.id.back_history)

        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }
    }
}
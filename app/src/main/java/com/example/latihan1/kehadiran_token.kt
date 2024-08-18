package com.example.latihan1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class kehadiran_token : Fragment() {
    lateinit var session: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kehadiran_token, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var clock = view.findViewById<TextView>(R.id.clock)
        var day = view.findViewById<TextView>(R.id.day)
        var date = view.findViewById<TextView>(R.id.date)
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        var connection = Connection()


        var calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)
        var dates = calendar.get(Calendar.DATE)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)
        var days = calendar.get(Calendar.DAY_OF_MONTH)

        clock.text = "$hour:$minute"
        day.text = "$days"
        date.text = "$dates $month $year"

        var token1 = view.findViewById<EditText>(R.id.token1)
        var token2 = view.findViewById<EditText>(R.id.token2)
        var token3 = view.findViewById<EditText>(R.id.token3)
        var token4 = view.findViewById<EditText>(R.id.token4)
        var token5 = view.findViewById<EditText>(R.id.token5)
        var check = true

//        while (check) {
//            if (token1.text.length != 0 && token2.text.length != 0 && token3.text.length != 0 && token4.text.length != 0 && token5.text.length != 0) {
//                val formatter = SimpleDateFormat("yyyy-MM-dd")
//                val date = Date()
//                val current = formatter.format(date)
//
//                var jsonObject = JSONObject().apply {
//                    put("latitude", "0.0")
//                    put("longtitude", "0.0")
//                    put("longtitude", "-")
//                    put("tanggal", current.toString())
//                    put("jam_masuk", "$hour:$minute")
//                    put("status", "hadir")
//                    put("id_siswa", session.getString("id", ""))
//                    put("mode", "Lokasi")
//                    put("token_masuk", "token_masuk")
//                }
//
//                lifecycleScope.launch {
//                    var result = postRequest(
//                        connection.connection + "kehadiran/absensi?token=" + session.getString(
//                            "token",
//                            ""
//                        ), jsonObject, null
//                    )
//
//                    result.fold(
//                        onSuccess = { response ->
//                            JSONObject(response)
//
//                            if (jsonObject.getBoolean("success")) {
//                                Toast.makeText(
//                                    requireContext().applicationContext,
//                                    "berhasil masuk",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            } else {
//                                Toast.makeText(
//                                    requireContext().applicationContext,
//                                    "anda berada di luar zona kehadiran!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        },
//                        onFailure = { error ->
//                            error.printStackTrace()
//                        }
//
//                    )
//                }
//                check = false
//            }
//        }

    }
}
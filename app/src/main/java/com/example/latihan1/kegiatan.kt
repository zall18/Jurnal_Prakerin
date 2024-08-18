package com.example.latihan1

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class kegiatan : Fragment() {
    lateinit var session: SharedPreferences
    lateinit var kegiatanadapter: kegiatanadapter
    lateinit var data: MutableList<kegiatanModel>
    lateinit var calendar: Calendar
    lateinit var awal: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kegiatan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var listView = view.findViewById<ListView>(R.id.listview_kegiatan)
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var connection = Connection()
        data = mutableListOf<kegiatanModel>()
        var id = session.getString("id", "")
        var token = session.getString("token", "")
        calendar = Calendar.getInstance()

        lifecycleScope.launch {

            var result = getRequest(connection.connection + "kegiatan/show/$id?token=$token", null)

            result.fold(
                onSuccess = {
                    response -> var jsonObject = JSONObject(response)
                    var jsonArray = jsonObject.getJSONArray("kegiatan")
                    for (i in 0 until jsonArray.length()){
                        var jsonObject2 = jsonArray.getJSONObject(i)
                        data.add(kegiatanModel(jsonObject2.getString("id"),jsonObject2.getString("created_at"), jsonObject2.getString("deskripsi"), jsonObject2.getString("durasi")))
                    }
                    kegiatanadapter = kegiatanadapter(requireContext().applicationContext, data)
                    listView.adapter = kegiatanadapter
                },
                onFailure = {

                }
            )

        }

        var fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(requireContext().applicationContext, kegiatanCreate::class.java))
        }

        var awal = view.findViewById<EditText>(R.id.tgl_awal)
        awal.setOnClickListener {
            showDatePicker()
        }


    }

    private fun showDatePicker(){
        val datePickerAnalog = DatePickerDialog(requireContext(), {DatePicker, year: Int, montthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, montthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatteDate = dateFormat.format(selectedDate.time)
            awal.setText(formatteDate)
        },
            calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerAnalog.show()
    }
}
package com.example.latihan1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.Calendar


class kepulangan_token : Fragment() {
    lateinit var session: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kepulangan_token, container, false)
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
    }
}
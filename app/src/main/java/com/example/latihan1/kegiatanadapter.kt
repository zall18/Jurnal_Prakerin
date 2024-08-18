package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class kegiatanadapter(val context: Context, val data: MutableList<kegiatanModel>): BaseAdapter() {

    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var session = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    var editor = session.edit()

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): Any {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1 ?: inflater.inflate(R.layout.kegiatana_item, null, false)

        var date = view.findViewById<TextView>(R.id.date_kegiatan)
        var activity = view.findViewById<TextView>(R.id.activity_kegiatan)
        var time = view.findViewById<TextView>(R.id.time_kegiatan)
        var view_kegiatan = view.findViewById<LinearLayout>(R.id.kegiatan_view)

        var kegiatans = getItem(p0) as kegiatanModel
        date.text = kegiatans.date
        activity.text = kegiatans.activity
        time.text = kegiatans.time

        view_kegiatan.setOnClickListener {
            editor.putString("idKegiatan", kegiatans.id)
            editor.commit()
            context.startActivity(Intent(context, detailKegiatan::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        return view
    }
}
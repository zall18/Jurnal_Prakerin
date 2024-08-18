package com.example.latihan1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class historyAdapter(val context: Context, val data: MutableList<historyModel>): BaseAdapter() {

    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
        var view = p1 ?: inflater.inflate(R.layout.historyitem, null, false)

        var date = view.findViewById<TextView>(R.id.date_history)
        var status = view.findViewById<TextView>(R.id.status_history)


        var historys = getItem(p0) as historyModel
        date.text = historys.date
        status.text = historys.status

        return view
    }
}
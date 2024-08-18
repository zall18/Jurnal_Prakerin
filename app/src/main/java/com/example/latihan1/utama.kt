package com.example.latihan1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class utama : Fragment() {

    lateinit var session:SharedPreferences
    lateinit var pagerAdapter: pagerAdapter
    lateinit var images : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_utama, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var nama = view.findViewById<TextView>(R.id.name_utama)
        var kelas = view.findViewById<TextView>(R.id.class_utama)
        var total_hadir = view.findViewById<TextView>(R.id.total_hadir)
        var total_izin = view.findViewById<TextView>(R.id.total_izin)
        var total_sakit = view.findViewById<TextView>(R.id.total_sakit)
        var total_jam = view.findViewById<TextView>(R.id.total_jam_kerja)
        var image = view.findViewById<ShapeableImageView>(R.id.image_siswa_utama)
        var pager = view.findViewById<ViewPager>(R.id.pager)
        var connection = Connection()
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var id = session.getString("id", "")
        var token = session.getString("token", "")

        Toast.makeText(requireContext(), "$id", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {

            var result = getRequest(connection.connection + "kehadiran/dashboard/$id?token=$token", null)

            result.fold(
                onSuccess = {
                    response -> var jsonObject = JSONObject(response)

                    if(!jsonObject.getString("siswa").isNullOrEmpty()){
                        var jsonObject2 = JSONObject(jsonObject.getString("siswa"))
                        nama.text = jsonObject2.getString("name")
                        kelas.text = jsonObject2.getString("kelas")
                        MainScope().launch {
                            var bitmap = getImageFromUrl(jsonObject2.getString("foto"))
                            image.setImageBitmap(bitmap)
                        }

                        total_hadir.text = jsonObject.getString("hadir")
                        total_izin.text = jsonObject.getString("izin")
                        total_sakit.text = jsonObject.getString("sakit")

                        var jsonObject3 = JSONObject(jsonObject.getString("total_jam_kerja"))
                        total_jam.text = jsonObject3.getString("jam") + " jam " + jsonObject3.getString("menit") + " menit "
                    }
                },
                onFailure = {
                    error -> error.printStackTrace()
                }
            )

            var result2 = getRequest(connection.connection + "banner/show?token=$token", null)

            result2.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)

                        var jsonArray = jsonObject.getJSONArray("banner")
                        images = mutableListOf<String>()
                        for (i in 0 until jsonArray.length()){
                            var jsonObject2 = jsonArray.getJSONObject(i)
                            images.add(jsonObject2.getString("gambar").toString())
                        }

                        pagerAdapter = pagerAdapter(requireContext().applicationContext, images)
                        pager.adapter = pagerAdapter


                },
                onFailure = {
                        error -> error.printStackTrace()
                }
            )

        }


    }
}
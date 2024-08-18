package com.example.latihan1

import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.util.Log


class kehadiran_lokasi : Fragment() {
    lateinit var session: SharedPreferences
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kehadiran_lokasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                var clock = view.findViewById<TextView>(R.id.clock)
        var day = view.findViewById<TextView>(R.id.day)
        var date = view.findViewById<TextView>(R.id.date)
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        var connection = Connection()
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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

        var pulang = view.findViewById<TextView>(R.id.pulang_button)
        var masuk = view.findViewById<LinearLayout>(R.id.kehadiran_button)
        masuk.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            val current = formatter.format(date)
            var longitude = 0.0
            var latitude = 0.0

            if (activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                activity?.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d("locations", "onViewCreated: $latitude $longitude")
                    }
                })
            }

            var jsonObject = JSONObject().apply {
                put("latitude", latitude.toString())
                put("longtitude", longitude.toString())
                put("tanggal", current.toString())
                put("jam_masuk", "$hour:$minute")
                put("status", "hadir")
                put("id_siswa", session.getString("idsiswa", ""))
                put("mode", "Lokasi")
                put("token_masuk", "token_masuk")
            }

            lifecycleScope.launch {
                var result = postRequest(connection.connection + "kehadiran/absensi?token=" + session.getString("token", ""), jsonObject, null )

                result.fold(
                    onSuccess = {
                        response -> var jsonObject2 = JSONObject(response)
                        Log.d("lokasi", "onViewCreated: $jsonObject2")

                        if(jsonObject2.getBoolean("success")){

                            editor.putString("idabsen", jsonObject2.getString("id"))
                            editor.commit()

                            Toast.makeText(
                                requireContext().applicationContext,
                                "berhasil masuk",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onFailure = {
                        error -> error.printStackTrace()
                        Toast.makeText(
                            requireContext().applicationContext,
                            "anda berada di luar zona kehadiran!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                )
            }

        }
    }
}
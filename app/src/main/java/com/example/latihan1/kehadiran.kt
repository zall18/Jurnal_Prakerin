package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date


class kehadiran : Fragment() {
    lateinit var session: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kehadiran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var pulang = view.findViewById<TextView>(R.id.pulang_button)
        var masuk = view.findViewById<TextView>(R.id.masuk_button)
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)

        var options = view.findViewById<ImageView>(R.id.option_absensi)
        options.setOnClickListener {
            popupMenu(options)
        }

        pulang.setOnClickListener{
            masuk.setBackgroundColor(resources.getColor(R.color.gray))
            masuk.setTextColor(resources.getColor(R.color.black))

            pulang.setBackgroundColor(resources.getColor(R.color.blue))
            pulang.setTextColor(resources.getColor(R.color.white))
            if (session.getString("mode_absensi", "") == "token"){
                replaceFragment(kepulangan_token())
            }else{
                replaceFragment(kepulangan_fragment())

            }
        }

        masuk.setOnClickListener {
            replaceFragment(kehadiran_lokasi())
            masuk.setBackgroundColor(resources.getColor(R.color.blue))
            masuk.setTextColor(resources.getColor(R.color.white))

            pulang.setBackgroundColor(resources.getColor(R.color.gray))
            pulang.setTextColor(resources.getColor(R.color.black))
            if (session.getString("mode_absensi", "") == "token"){
                replaceFragment(kehadiran_token())
            }else{
                replaceFragment(kehadiran_lokasi())

            }
        }

        var form: AppCompatButton = view.findViewById(R.id.formulir_button)

        form.setOnClickListener {
            startActivity(Intent(requireContext().applicationContext, formulir2::class.java))
        }
        replaceFragment(kehadiran_lokasi())
        if (session.getString("mode_absensi", "") == "token"){
            replaceFragment(kehadiran_token())
        }



    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.kehadiran_lokasi, fragment).commit()
    }

    private fun popupMenu(view: View) {
        var popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.absensi, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
                item ->
            when(item.itemId){
                R.id.history -> {
                    startActivity(Intent(requireContext(), historyKehadiran::class.java))
                    true
                }
                else -> false
            }
        }

        popupMenu.setOnDismissListener {
            popupMenu.dismiss()
        }

        popupMenu.show()
    }

}
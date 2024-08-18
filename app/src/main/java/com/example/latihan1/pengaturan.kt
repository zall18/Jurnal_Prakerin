package com.example.latihan1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class pengaturan : Fragment() {
    lateinit var session: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pengaturan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var nama = view.findViewById<TextView>(R.id.name_pengaturan)
        var kelas = view.findViewById<TextView>(R.id.class_pengaturan)
        var nisn = view.findViewById<TextView>(R.id.nisn_pengaturan)
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        var id = session.getString("id", "")
        var token = session.getString("token", "")
        var image = view.findViewById<ShapeableImageView>(R.id.image_siswa_pengaturan)
        var connection = Connection()

        lifecycleScope.launch {

            var result =
                getRequest(connection.connection + "auth/show/$id?token=$token", null)

            result.fold(
                onSuccess = { response ->
                    var jsonObject = JSONObject(response)

                    if (jsonObject.getBoolean("success")) {
                        var jsonObject2 = JSONObject(jsonObject.getString("user"))
                        nama.text = jsonObject2.getString("name")
                        kelas.text = jsonObject2.getString("kelas")
                        MainScope().launch {
                            var bitmap = getImageFromUrl(jsonObject2.getString("foto"))
                            image.setImageBitmap(bitmap)
                        }



                    }
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }
        var edit = view.findViewById<LinearLayout>(R.id.edit_akun)
        edit.setOnClickListener {
            startActivity(Intent(requireContext().applicationContext, editAkun::class.java))
        }

        var mode = view.findViewById<LinearLayout>(R.id.mode_absensi)
        var lokasi_mode = view.findViewById<RadioButton>(R.id.lokasi)
        var token_mode = view.findViewById<RadioButton>(R.id.token)
        var radio_group = view.findViewById<RadioGroup>(R.id.mode_group)
        mode.setOnClickListener {
            radio_group.visibility = View.VISIBLE

            if(session.getString("mode_absensi", "") == "token"){
                token_mode.isChecked = true

            }else{
                lokasi_mode.isChecked = true

            }
        }

        lokasi_mode.setOnClickListener {
            editor.putString("mode_absensi", "lokasi")
            editor.commit()
        }

        token_mode.setOnClickListener {
            editor.putString("mode_absensi", "token")
            editor.commit()
        }

        var logout = view.findViewById<ImageView>(R.id.logout)

        logout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog.setTitle("Logout")
            alertDialog.setMessage("Are you sure to logout?")
            alertDialog.setPositiveButton("Yes") {_, _ ->
                /*startActivity(Intent(requireContext().applicationContext, MainActivity::class.java))*/

                lifecycleScope.launch {
                    var result = getRequest(connection.connection + "auth/logout?token=$token", null)

                    result.fold(
                        onSuccess = {
                            response -> var jsonObject = JSONObject(response)
                            if(jsonObject.getBoolean("success")){
                                startActivity(Intent(requireContext().applicationContext, MainActivity::class.java))
                            }
                        },
                        onFailure = {

                        }
                    )
                }
            }
            alertDialog.setNegativeButton("No"){_, _ ->

            }
            alertDialog.create()
            alertDialog.show()
        }

        var edit_profil = view.findViewById<LinearLayout>(R.id.edit_profile)
        edit_profil.setOnClickListener {
            startActivity(Intent(requireContext(), editProfil::class.java))
        }


    }
}
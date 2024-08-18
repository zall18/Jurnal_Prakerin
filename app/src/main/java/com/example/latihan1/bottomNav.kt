package com.example.latihan1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class bottomNav : AppCompatActivity() {


    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bottom_nav)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            item ->
            when(item.itemId){
                R.id.utama -> {
                    replaceFragment(utama())
                    true
                }
                R.id.kehadiran -> {
                    replaceFragment(kehadiran())
                    true
                }
                R.id.kegiatan -> {
                    replaceFragment(kegiatan())
                    true
                }
                R.id.pengaturan -> {
                    replaceFragment(pengaturan())
                    true
                }
                else -> false
            }
        }
        replaceFragment(utama())

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
package com.masdika.espandroidfirebase

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masdika.espandroidfirebase.databinding.ActivityMainBinding
import nl.joery.animatedbottombar.AnimatedBottomBar
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference()

        // Add ValueEventListener to fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val ambientTemp = snapshot.child("MLX90614").child("ambient_temp").getValue()
                val objectTemp = snapshot.child("MLX90614").child("object_temp").getValue()
                val gpsLat = snapshot.child("GPS").child("latitude").getValue()
                val gpsLng = snapshot.child("GPS").child("longitude").getValue()
                val heartRate = snapshot.child("MAX30100").child("heart_rate").getValue()
                val spo2 = snapshot.child("MAX30100").child("spo2").getValue()

//                binding.tvAmbientTemp.text = "Ambient Temperature : ${String.format("%.2f", ambientTemp)}°C"
//                binding.tvObjectTemp.text = "Object Temperature : ${String.format("%.2f", objectTemp)}°C"
//                binding.tvLatitude.text = "Latitude : ${gpsLat.toString()}"
//                binding.tvLongitude.text = "Long : ${gpsLng.toString()}"
//                binding.tvHeartrate.text = "Heart Rate : ${heartRate.toString()} BPM"
//                binding.tvBloodO2.text = "Blood O2 : ${spo2.toString()}%"

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })


        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                Log.d("Bottom_Bar", "Selected index: $newIndex, title: ${newTab.title}")
                when (newTab.id) {
                    R.id.tab_profile -> {
                        binding.mapView.visibility = View.VISIBLE

                        // remove Fragment History
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragment = fragmentManager.findFragmentById(R.id.frame_layout)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(it).commit()
                            Log.d("Remove Fragment", "Fragment : $fragment")
                        }
                    }

                    R.id.tab_history -> {
                        // Disable mapview
                        binding.mapView.visibility = View.GONE

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, HistoryFragment()).commit()
                    }

                }
            }
        })

        binding.openDialog.setOnClickListener {
            showBottomSheet()
        }

    }

    private fun showBottomSheet() {
        dialog = BottomSheetDialog(this)
        dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<ImageView>(R.id.close_button)
        val tvTemperature = view.findViewById<TextView>(R.id.tv_temperature)
        val tvHearRate = view.findViewById<TextView>(R.id.tv_heart_rate)
        val tvBloodOxygen = view.findViewById<TextView>(R.id.tv_blood_oxygen)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val objectTemp = snapshot.child("MLX90614").child("object_temp").getValue()
                val heartRate = snapshot.child("MAX30100").child("heart_rate").getValue()
                val bloodOxygen = snapshot.child("MAX30100").child("spo2").getValue()

                tvTemperature.text = "${(objectTemp as Double).roundToInt()}°C"
                tvHearRate.text = "${heartRate} bpm"
                tvBloodOxygen.text = "${bloodOxygen}%"

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })


        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

}
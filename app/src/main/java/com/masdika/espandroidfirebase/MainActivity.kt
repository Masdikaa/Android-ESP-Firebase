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

        database = FirebaseDatabase.getInstance().getReference()

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
                        binding.openDialog.visibility = View.VISIBLE
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragment = fragmentManager.findFragmentById(R.id.frame_layout)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(it).commit()
                            Log.d("Remove Fragment", "Fragment : $fragment")
                        }
                    }

                    R.id.tab_history -> {
                        binding.mapView.visibility = View.GONE
                        binding.openDialog.visibility = View.GONE
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
        val tvHeartRate = view.findViewById<TextView>(R.id.tv_heart_rate)
        val tvBloodOxygen = view.findViewById<TextView>(R.id.tv_blood_oxygen)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        updateBodyCondition(tvTemperature, tvHeartRate, tvBloodOxygen)

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun updateBodyCondition(
        tvTemperature: TextView,
        tvHeartRate: TextView,
        tvBloodOxygen: TextView
    ) {

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val objectTemp = snapshot.child("MLX90614").child("object_temp").getValue()
                val heartRate = snapshot.child("MAX30100").child("heart_rate").getValue()
                val bloodOxygen = snapshot.child("MAX30100").child("spo2").getValue()

                tvTemperature.text = "${(objectTemp as Double).roundToInt()}°C"
                tvHeartRate.text = "${heartRate} bpm"
                tvBloodOxygen.text = "${bloodOxygen}%"

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

    }

    private fun updateGeoLocation() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val gpsLat = snapshot.child("GPS").child("latitude").getValue()
                val gpsLng = snapshot.child("GPS").child("longitude").getValue()

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}
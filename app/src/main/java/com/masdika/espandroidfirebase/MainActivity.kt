package com.masdika.espandroidfirebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masdika.espandroidfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

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

                // binding.tvAmbientTemp.text = "Ambient Temperature : ${ambientTemp.toString()}"
                binding.tvAmbientTemp.text = "Ambient Temperature : ${String.format("%.2f", ambientTemp)}°C"
                binding.tvObjectTemp.text = "Object Temperature : ${String.format("%.2f", objectTemp)}°C"
                binding.tvLatitude.text = "Latitude : ${gpsLat.toString()}"
                binding.tvLongitude.text = "Long : ${gpsLng.toString()}"
                binding.tvHeartrate.text = "Heart Rate : ${heartRate.toString()} BPM"
                binding.tvBloodO2.text = "Blood O2 : ${spo2.toString()}%"

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}
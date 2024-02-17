package com.masdika.espandroidfirebase

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
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
        database = FirebaseDatabase.getInstance().getReference("MLX90614")

        // Add ValueEventListener to fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val ambientTemp = snapshot.child("ambient_temp").value
                val objectTemp = snapshot.child("object_temp").value

                val value = snapshot.getValue<String>()
                Log.d("Database Value", "Value is: $value")
                Log.e("Data 1", "$ambientTemp")
                Log.e("Data 2", "$objectTemp")

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}
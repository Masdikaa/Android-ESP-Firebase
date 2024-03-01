package com.masdika.espandroidfirebase

import android.graphics.Rect
import android.location.GpsStatus
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
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), MapListener, GpsStatus.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var dialog: BottomSheetDialog

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay

    private var anotherOverlayItemArray: ArrayList<OverlayItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //START MAP+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        // SetViewMap
        mMap = binding.mapview
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())
        mMap.setBuiltInZoomControls(false)

        // Define My Location overlay
        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller

        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                controller.setCenter(mMyLocationOverlay.myLocation)
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        // Define User Location Overlay
        anotherOverlayItemArray.add(OverlayItem("User", "User", GeoPoint(-7.640658, 111.517252)))

        val anotherItemizedIconOverlay =
            ItemizedIconOverlay<OverlayItem>(this, anotherOverlayItemArray, null)
        mMap.overlays.add(anotherItemizedIconOverlay)

        controller.setZoom(13.0)

        mMap.overlays.add(mMyLocationOverlay)
        val myScaleBarOverlay = ScaleBarOverlay(mMap)
        mMap.overlays.add(myScaleBarOverlay)
        mMap.addMapListener(this)
        //END MAP+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
                        binding.mapview.visibility = View.VISIBLE
                        binding.openDialog.visibility = View.VISIBLE
                        binding.mapsIntent.visibility = View.VISIBLE
                        val fragmentManager: FragmentManager = supportFragmentManager
                        val fragment = fragmentManager.findFragmentById(R.id.frame_layout)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(it).commit()
                            Log.d("Remove Fragment", "Fragment : $fragment")
                        }
                    }

                    R.id.tab_history -> {
                        binding.mapview.visibility = View.GONE
                        binding.openDialog.visibility = View.GONE
                        binding.mapsIntent.visibility = View.GONE
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
        //binding.mapsIntent.visibility = View.GONE
        dialog = BottomSheetDialog(this)
        dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)

        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<ImageView>(R.id.close_button)
        val tvTemperature = view.findViewById<TextView>(R.id.tv_temperature)
        val tvHeartRate = view.findViewById<TextView>(R.id.tv_heart_rate)
        val tvBloodOxygen = view.findViewById<TextView>(R.id.tv_blood_oxygen)

        btnClose.setOnClickListener {
            dialog.dismiss()
            //binding.mapsIntent.visibility = View.VISIBLE
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

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onCreate:la ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.getMapCenter()?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onGpsStatusChanged(p0: Int) {

    }

}

//binding.openMap.setOnClickListener {
//    val latitude = -7.868472
//    val longitude = -248.527789
//    val label = "Lokasi yang Dituju"
//    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
//    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//    mapIntent.setPackage("com.google.android.apps.maps")
//    startActivity(mapIntent)
//}
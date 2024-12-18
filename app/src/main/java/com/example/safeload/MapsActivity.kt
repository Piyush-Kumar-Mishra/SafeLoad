package com.example.safeload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var btnSubmitLocation: Button
    private lateinit var searchView: SearchView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapView = findViewById(R.id.mapView)
        btnSubmitLocation = findViewById(R.id.btnSubmitLocation)
        searchView = findViewById(R.id.searchView)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnSubmitLocation.setOnClickListener {
            val intent = Intent()
            intent.putExtra("location", selectedLocation?.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setOnMapClickListener { location ->
            selectedLocation = location
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(location).title("Selected Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }

        // Enable the current location button if permission is granted
        enableCurrentLocation()
    }

    private fun enableCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            moveToCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun moveToCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(query: String) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a location to search", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val geocoder = Geocoder(this)
            val addresses = geocoder.getFromLocationName(query, 1)

            // Check if the result is not null or empty
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)

                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(location).title(query))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

                selectedLocation = location
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error finding location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableCurrentLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}

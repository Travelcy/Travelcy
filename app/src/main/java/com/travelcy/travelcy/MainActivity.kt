package com.travelcy.travelcy

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.travelcy.travelcy.database.TravelcyDatabase
import com.travelcy.travelcy.services.location.LocationRepository


class MainActivity : AppCompatActivity() {
    lateinit var locationRepository: LocationRepository
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (hasLocationPermissions()) {
            startWatchingLocation()
        }

        val application = getApplication() as MainApplication
        locationRepository = LocationRepository(this,fusedLocationProviderClient , application.getCurrencyRepository())
    }

    @SuppressLint("MissingPermission")
    fun startWatchingLocation() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, object: LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
                Log.d("MainActivity", "Location available")
            }
        } , null)
    }

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_ID)
    }

    fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_ID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startWatchingLocation()
                    // the request was granted so we update the currency from location
                   locationRepository.updateForeignCurrencyFromLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }

    }

    override fun onDestroy() {
        TravelcyDatabase.getInstance(this).close()

        super.onDestroy()
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_ID = 1
    }
}
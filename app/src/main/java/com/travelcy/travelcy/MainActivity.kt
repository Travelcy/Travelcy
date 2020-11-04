package com.travelcy.travelcy

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.travelcy.travelcy.database.TravelcyDatabase
import com.travelcy.travelcy.services.location.LocationRepository




class MainActivity : AppCompatActivity() {
    lateinit var locationRepository: LocationRepository
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var requestingLocationUpdates = false
    private var updateOnNextLocationUpdate = false
    lateinit var connectivityManager: ConnectivityManager
    private lateinit var mainApplication: MainApplication



    init {
        instance = this
    }

    private val networkChangedReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive intent $intent")

            when(intent?.action) {
                ConnectivityManager.CONNECTIVITY_ACTION -> handleNetworkChanged()
            }
        }
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationAvailability(p0: LocationAvailability?) {
            super.onLocationAvailability(p0)
            Log.d(TAG, "onLocationAvailability")
            if (updateOnNextLocationUpdate) {
                updateOnNextLocationUpdate = false
                locationRepository.updateForeignCurrencyFromLocation()
            }
        }
    }

    fun isNetworkConnected(): Boolean {
        Log.d(TAG, "isNetworkConnected")
        val capabilities: NetworkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?: return false

        if (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        ) {
            return true
        }

        return false
    }

    fun handleNetworkChanged() {
        Log.d(TAG, "handleNetworkChanged")
        val networkConnected = isNetworkConnected()
        Log.d(TAG, "Is network connected? $networkConnected")
        mainApplication.getCurrencyRepository().setNetworkConnected(isNetworkConnected())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        mainApplication = application as MainApplication

        if (mainApplication.appLoaded) {
            setupView()
        }

        connectivityManager = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        if (hasLocationPermissions()) {
            startWatchingLocation()
        }

        val application = application as MainApplication
        locationRepository = LocationRepository(this,fusedLocationProviderClient , application.getCurrencyRepository())
    }

    fun onAppLoaded() {
        if (!mainApplication.appLoaded) {
            mainApplication.appLoaded = true
            setupView()
        }
    }

    fun setupView() {
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        setTheme(R.style.AppTheme)
    }

    override fun onResume() {
        Log.d(TAG, "onResume, restarting location updates")
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangedReceiver, intentFilter)
        handleNetworkChanged()
    }

    override fun onPause() {
        Log.d(TAG, "onPause, stopping location updates")
        super.onPause()
        stopLocationUpdates()
        unregisterReceiver(networkChangedReceiver)
    }

    private fun stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates")
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        requestingLocationUpdates = true
        Log.d(TAG, "startLocationUpdates()")

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback , Looper.getMainLooper())
    }

    private fun startWatchingLocation() {
        Log.d(TAG, "startWatchingLocationupdateWhenLocationAvailable")
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        Log.d(TAG, "Checking location settings")
        task.addOnSuccessListener { locationSettingsResponse ->
            Log.d(TAG, "Location settings OK")
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            Log.d(TAG, "Location settings failed")
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    Log.d(TAG, "Showing user dialog")
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this@MainActivity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun requestLocationPermissions() {
        Log.d(TAG, "requestLocationPermissions")
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_ID)
    }

    fun hasLocationPermissions(): Boolean {
        Log.d(TAG, "hasLocationPermissions")
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
        Log.d(TAG, "onRequestPermissionsResult")
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_ID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    updateOnNextLocationUpdate = true
                    locationRepository.updateForeignCurrencyFromLocation()
                    // the request was granted so we update the currency from location
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
        TravelcyDatabase.close(this)

        super.onDestroy()
    }

    companion object {
        var instance: MainActivity? = null
        private set
        const val TAG = "MainActivity"
        const val LOCATION_PERMISSION_REQUEST_ID = 1
        const val LOCATION_INTERVAL: Long = 10000
        const val LOCATION_FASTEST_INTERVAL: Long = 5000
        const val REQUEST_CHECK_SETTINGS = 9000
    }
}
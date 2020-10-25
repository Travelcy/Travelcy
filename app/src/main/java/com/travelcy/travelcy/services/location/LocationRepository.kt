package com.travelcy.travelcy.services.location

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.travelcy.travelcy.MainActivity
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.services.currency.CurrencyRepository
import java.util.Locale
import java.util.Currency as JavaCurrency


class LocationRepository(private val activity: MainActivity, private val fusedLocationProviderClient: FusedLocationProviderClient, private val currencyRepository: CurrencyRepository){
    private val currencies = currencyRepository.currencies

    private fun getCurrencyFromLocation(location: Location): Currency? {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1);

        if (address.isEmpty()) {
            return null;
        }

        val currencyCode = JavaCurrency.getInstance(Locale("", address[0].countryCode))?.currencyCode

        if (currencies.value?.isNotEmpty() == true) {
            return currencies.value?.find {
                currency -> currency.id == currencyCode
            }
        }

        return null
    }

    @SuppressLint("MissingPermission")
    fun updateForeignCurrencyFromLocation() {
        Log.d(TAG, "updateForeignCurrencyFromLocation")
        if (!activity.hasLocationPermissions()
        ) {
            Log.d(TAG, "Requesting permissions")
            activity.requestLocationPermissions()
        }
        else {
            Log.d(TAG, "Waiting for location")
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    Log.d(TAG, "Got location $it")
                    val currency = getCurrencyFromLocation(it)
                    if (currency != null) {
                        currencyRepository.changeForeignCurrency(currency.id)
                    }
                    else {
                        Log.d(TAG, "Could not convert location to currency")
                    }
                }
                else {
                    // TODO show error or request that location is turned on
                    Log.d(TAG, "Location is not available")
                }
            }
        }
    }

    companion object {
        const val TAG = "LocationRepository"
    }
}
package com.travelcy.travelcy.services.location

import android.content.Context
import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.database.dao.LocationDao
import com.travelcy.travelcy.model.Location
import java.util.*
import java.util.concurrent.Executor

class LocationRepository(private val locationDao: LocationDao, private val executor: Executor){

    val currentLocation: LiveData<Location> = locationDao.getLocation()

    private fun getLocation():Location {
        // get country
        val locale = Locale("", getCountryCode()!!)
        val country = locale.country

        // get currency code
        val currencyCode = Currency.getInstance(locale).currencyCode

        // create and return new location with country and currency code
        return Location(1, country, currencyCode)
    }

    private fun getCountryCode(context: Context = MainApplication.applicationContext()): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        if (simCountry != null && simCountry.length == 2) { // SIM country code is available
            return simCountry
        } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // Device is not 3G (would be unreliable)
            val networkCountry = tm.networkCountryIso
            if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                return networkCountry
            }
        }

        return null
    }

    private fun updateLocation(location: Location) {
        executor.execute {
            locationDao.updateLocation(location)
        }
    }

    init {
        currentLocation.observeForever {
            if (it != null && it.country != currentLocation.value?.country) {
                updateLocation(it)
            }
        }
        executor.execute {
            if (!locationDao.hasLocation()) {
                // Setup initial location
                updateLocation(getLocation())
            }
        }
    }
}
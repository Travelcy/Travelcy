package com.travelcy.travelcy.services.location

import androidx.lifecycle.LiveData
import com.travelcy.travelcy.database.dao.LocationDao
import com.travelcy.travelcy.model.Location
import java.util.Locale
import java.util.Currency
import java.util.concurrent.Executor

class LocationRepository (private val locationDao: LocationDao, private val executor: Executor){

    val currentLocation: LiveData<Location> = locationDao.getLocation()

    private fun getGPSLocation():Location {
        // get country
        val locale = Locale.getDefault()
        val country = locale.country

        // get currency code
        val currencyCode = Currency.getInstance(locale).currencyCode

        // create and return new location with country and currency code
        return Location(1, country, currencyCode)
    }

    private fun updateLocation(location:Location) {
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
                updateLocation(getGPSLocation())
            }
        }
    }
}
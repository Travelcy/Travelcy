package com.travelcy.travelcy.services.location

import com.travelcy.travelcy.database.dao.LocationDao
import com.travelcy.travelcy.model.Location
import java.util.Locale
import java.util.Currency
class LocationRepository (private val locationDao: LocationDao){

    private val currentLocation:Location? = locationDao.getLocation()

    private fun getGPSLocation():Location {
        // get country
        val locale = Locale.getDefault()
        val country = locale.country

        // get currency code
        val currencyCode = Currency.getInstance(locale).currencyCode

        // create and return new location with country and currency code
        return Location(1, country, currencyCode)
    }

    fun getCurrentLocation():Location {
        val gpsLocation = getGPSLocation()
        // If the currentLocation hasn't been set, or the location is different - update it
        if(currentLocation == null || gpsLocation.country !== currentLocation.country) {
            locationDao.updateLocation(gpsLocation)
        }

        return currentLocation as Location
    }
}
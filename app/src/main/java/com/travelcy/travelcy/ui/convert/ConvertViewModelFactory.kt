package com.travelcy.travelcy.ui.convert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.location.LocationRepository

class ConvertViewModelFactory(private val currencyRepository: CurrencyRepository, private val locationRepository: LocationRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CurrencyRepository::class.java, LocationRepository::class.java).newInstance(currencyRepository, locationRepository)
    }
}
package com.travelcy.travelcy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.services.currency.CurrencyRepository

class SettingsViewModelFactory(private val currencyRepository: CurrencyRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CurrencyRepository::class.java).newInstance(currencyRepository)
    }
}
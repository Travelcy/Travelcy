package com.travelcy.travelcy.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.services.currency.CurrencyRepository

class SettingsViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {
    val currencies = currencyRepository.currencies
}
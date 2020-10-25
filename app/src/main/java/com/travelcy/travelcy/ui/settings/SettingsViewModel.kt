package com.travelcy.travelcy.ui.settings

import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.services.currency.CurrencyRepository

class SettingsViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {
    val currencies = currencyRepository.currencies

    fun updateCurrency(currency: Currency) {
        currencyRepository.updateCurrency(currency)
    }

    fun updateCurrencies(currencies: List<Currency>) {
        currencyRepository.updateCurrencies(currencies)
    }
}
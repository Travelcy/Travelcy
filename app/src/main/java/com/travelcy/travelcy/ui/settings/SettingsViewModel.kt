package com.travelcy.travelcy.ui.settings

import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.settings.SettingsRepository
import java.math.RoundingMode
import java.text.DecimalFormat

class SettingsViewModel(private val currencyRepository: CurrencyRepository, private val billRepository: BillRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
    val currencies = currencyRepository.currencies
    val defaultPerson = billRepository.defaultPerson
    val exchangeRatesLastUpdated = settingsRepository.exchangeRatesLastUpdated
    val autoUpdateExchangeRates = settingsRepository.autoUpdateExchangeRates

    fun updateCurrency(currency: Currency) {
        currencyRepository.updateCurrency(currency)
    }

    fun updateCurrencies(currencies: List<Currency>) {
        currencyRepository.updateCurrencies(currencies)
    }

    fun refreshCurrencies() {
        currencyRepository.refreshCurrencies()
    }

    fun updateBudget(amount: String) {
        if (defaultPerson.value != null) {
            defaultPerson.value!!.budget = amount.toDouble()
            billRepository.updatePerson(defaultPerson.value!!)
        }
    }

    fun formatAmount(amount: Double): String {
        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.CEILING
        return decimalFormat.format(amount)
    }

    fun updateAutoUpdateExchangeRates(autoUpdate: Boolean) {
        settingsRepository.updateAutoUpdateExchangeRates(autoUpdate)
    }
}
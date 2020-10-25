package com.travelcy.travelcy.services.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.Currency

class CurrencyLiveData (
    private val currencyCode: LiveData<String>,
    private val currencies: LiveData<List<Currency>>
): MediatorLiveData<Currency>() {
    private fun updateCurrency(code: String?, allCurrencies: List<Currency>?) {
        value = allCurrencies?.find {
            it.id == code
        }
    }

    init {
        addSource(currencyCode) {
            updateCurrency(it, currencies.value)
        }

        addSource(currencies) {
            updateCurrency(currencyCode.value, it)
        }
    }
}
package com.travelcy.travelcy.services.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.Currency

class LocalCurrencyIdsLiveData (
    private val currencyIds: LiveData<List<String>>,
    private val localCurrency: LiveData<Currency>,
    private val networkConnected: LiveData<Boolean>
): MediatorLiveData<List<String>>() {
    private fun updateLocalCurrencies(currencies: List<String>?, localCurrency: Currency?, networkConnected: Boolean?) {
        value = if (networkConnected == true) {
            println("Sending out currencies")
            currencies ?: emptyList()
        } else {
            if (localCurrency != null) {
                println("Sending out local currency")
                listOf(localCurrency.id)
            } else {
                println("Sending out empty list")
                emptyList()
            }
        }
    }

    init {
        addSource(currencyIds) {
            println("currency ids changed")
            updateLocalCurrencies(it, localCurrency.value, networkConnected.value)
        }

        addSource(localCurrency) {
            println("local currency changed")
            updateLocalCurrencies(currencyIds.value, it, networkConnected.value)
        }

        addSource(networkConnected) {
            updateLocalCurrencies(currencyIds.value, localCurrency.value, it)
        }
    }
}
package com.travelcy.travelcy.ui.convert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.Currency

class LocalAmountLiveData(
    private val foreignAmount: LiveData<Double>,
    private val foreignCurrency: LiveData<Currency>
): MediatorLiveData<Double>() {
    fun updateLocalAmount(foreignAmount: Double?, foreignCurrency: Currency?) {
        value = if (foreignAmount == null  || foreignCurrency == null || foreignCurrency.exchangeRate == 0.0) {
            0.0
        } else {
            foreignAmount / foreignCurrency.exchangeRate
        }
    }

    init {
        addSource(foreignCurrency) {
            updateLocalAmount(foreignAmount.value, it)
        }

        addSource(foreignAmount) {
            updateLocalAmount(it, foreignCurrency.value)
        }
    }
}
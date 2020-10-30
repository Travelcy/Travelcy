package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.utils.FormatUtils

class FormatPriceLiveData(
    private val foreignAmount: LiveData<Double>,
    private val localCurrency: LiveData<Currency>,
    private val foreignCurrency: LiveData<Currency>
): MediatorLiveData<String>() {
    private fun recalculateTotal(foreignAmount: Double?, localCurrency: Currency?, foreignCurrency: Currency?) {
        value = FormatUtils.formatPrice(foreignAmount ?: 0.0, localCurrency, foreignCurrency)
    }

    init {
        addSource(foreignAmount) {
            recalculateTotal(it, localCurrency.value, foreignCurrency.value)
        }

        addSource(localCurrency) {
            recalculateTotal(foreignAmount.value, it, foreignCurrency.value)
        }

        addSource(foreignCurrency) {
            recalculateTotal(foreignAmount.value, localCurrency.value, it)
        }
    }
}
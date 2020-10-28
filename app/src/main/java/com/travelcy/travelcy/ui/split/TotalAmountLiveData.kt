package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.BillItemWithPersons
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.utils.FormatUtils

class TotalAmountLiveData(
    private val billItemsWithPersons: LiveData<List<BillItemWithPersons>>,
    private val localCurrency: LiveData<Currency>,
    private val foreignCurrency: LiveData<Currency>
): MediatorLiveData<String>() {
    private fun recalculateTotal(billItems: List<BillItemWithPersons>?, localCurrency: Currency?, foreignCurrency: Currency?) {
        val foreignAmount = billItems?.sumByDouble { it.billItem.amount * it.billItem.quantity } ?: 0.0

        value = FormatUtils.formatPrice(foreignAmount, localCurrency, foreignCurrency)
    }

    init {
        addSource(localCurrency) {
            recalculateTotal(billItemsWithPersons.value, it, foreignCurrency.value)
        }

        addSource(foreignCurrency) {
            recalculateTotal(billItemsWithPersons.value, localCurrency.value, it)
        }

        addSource(billItemsWithPersons) {
            recalculateTotal(it, localCurrency.value, foreignCurrency.value)
        }
    }
}
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
    private fun recalculateTotal(billItems: List<BillItemWithPersons>?, currency: com.travelcy.travelcy.model.Currency?) {
        val localAmount = billItems?.sumByDouble { it.billItem.amount * it.billItem.quantity } ?: 0.0

        value = FormatUtils.formatPrice(localAmount, localCurrency.value, currency)
    }

    init {
        addSource(foreignCurrency) {
            recalculateTotal(billItemsWithPersons.value, it)
        }

        addSource(billItemsWithPersons) {
            recalculateTotal(it, foreignCurrency.value)
        }
    }
}
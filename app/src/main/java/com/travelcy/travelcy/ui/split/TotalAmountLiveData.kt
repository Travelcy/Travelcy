package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class TotalAmountLiveData(
    private val totalAmount: LiveData<Double>,
    private val tax: LiveData<Double>,
    private val tip: LiveData<Double>
): MediatorLiveData<Double>() {
    private fun recalculateTotal(totalAmount: Double, tax: Double, tip: Double) {
        value = totalAmount + tax + tip
    }

    init {
        addSource(totalAmount) {
            recalculateTotal(it, tax.value ?: 0.0, tip.value ?: 0.0)
        }

        addSource(tax) {
            recalculateTotal(totalAmount.value ?: 0.0, it, tip.value ?: 0.0)
        }

        addSource(tip) {
            recalculateTotal(totalAmount.value ?: 0.0, tax.value ?: 0.0, it)
        }
    }
}
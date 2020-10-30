package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class TipPercentageLiveData(
    private val total: LiveData<Double>,
    private val billTipPercentage: LiveData<Double?>,
    private val billTipAmount: LiveData<Double?>
): MediatorLiveData<Double>() {
    private fun calculateTipPercentage(total: Double, billTipPercentage: Double, billTipAmount: Double?) {
        value = if (billTipAmount == null) {
            billTipPercentage
        }
        else {
            billTipAmount / total
        }
    }

    init {
        addSource(total) {
            calculateTipPercentage(it, billTipPercentage.value ?: 0.0, billTipAmount.value)
        }

        addSource(billTipPercentage) {
            calculateTipPercentage(total.value ?: 0.0, it ?: 0.0, billTipAmount.value)
        }

        addSource(billTipAmount) {
            calculateTipPercentage(total.value ?: 0.0, billTipPercentage.value ?: 0.0, it)
        }
    }
}
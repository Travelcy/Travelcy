package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class PercentageLiveData(
    private val amount: LiveData<Double>,
    private val percentage: LiveData<Double?>
): MediatorLiveData<Double>() {
    private fun addPercentageToTotal(amount: Double?, percentage: Double?) {
        value = if (percentage == null || amount == null) {
            0.0
        } else {
            amount * percentage
        }
    }

    init {
        addSource(amount) {
            addPercentageToTotal(it, percentage.value)
        }

        addSource(percentage) {
            addPercentageToTotal(amount.value, it)
        }
    }
}
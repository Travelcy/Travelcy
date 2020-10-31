package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class LiveDataSum(
    private val amount1: LiveData<Double>,
    private val amount2: LiveData<Double>
): MediatorLiveData<Double>() {
    init {
        addSource(amount1) {
            value = it + (amount2.value ?: 0.0)
        }

        addSource(amount2) {
            value = (amount1.value ?: 0.0) + it
        }
    }
}
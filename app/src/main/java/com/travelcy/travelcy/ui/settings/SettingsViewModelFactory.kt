package com.travelcy.travelcy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository

class SettingsViewModelFactory(private val currencyRepository: CurrencyRepository, private val billRepository: BillRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CurrencyRepository::class.java, BillRepository::class.java).newInstance(currencyRepository, billRepository)
    }
}
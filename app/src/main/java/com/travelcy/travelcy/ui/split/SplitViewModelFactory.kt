package com.travelcy.travelcy.ui.split

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository

class SplitViewModelFactory(private val billRepository: BillRepository, private val currencyRepository: CurrencyRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(BillRepository::class.java, CurrencyRepository::class.java).newInstance(billRepository, currencyRepository)
    }
}
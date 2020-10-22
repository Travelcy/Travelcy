package com.travelcy.travelcy.ui.split

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.travelcy.travelcy.services.bill.BillRepository

class SplitViewModelFactory(private val billRepository: BillRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(BillRepository::class.java).newInstance(billRepository)
    }
}
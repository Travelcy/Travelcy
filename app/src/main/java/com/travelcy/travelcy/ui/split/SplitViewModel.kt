package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.BillItemWithPersons
import com.travelcy.travelcy.services.bill.BillRepository

class SplitViewModel(private val billRepository: BillRepository) : ViewModel() {

    val billItemsWithPersons = billRepository.billItemsWithPersons

    val totalAmount = Transformations.map(billItemsWithPersons) { billItems ->
        billItems?.sumByDouble { it.billItem.amount * it.billItem.quantity }.toString()
    }

    fun addBillItem(billItem: BillItem): Int {
        return billRepository.addBillItem(billItem)
    }

    fun updateBillItem(billItem: BillItem) {
        return billRepository.updateBillItem(billItem)
    }

    fun getBillItem(billItemId: Int): LiveData<BillItemWithPersons> {
        return billRepository.getBillItem(billItemId)
    }

    fun deleteBillItem(billItem: BillItem) {
        return billRepository.deleteBillItem(billItem)
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Split Fragment"
    }
    val text: LiveData<String> = _text
}
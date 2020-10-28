package com.travelcy.travelcy.ui.split

import androidx.lifecycle.*
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Person
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.utils.FormatUtils

class SplitViewModel(private val billRepository: BillRepository, private val currencyRepository: CurrencyRepository) : ViewModel() {

    val billItemsWithPersons = billRepository.billItems
    val persons = billRepository.persons

    private val foreignCurrency: LiveData<Currency> = currencyRepository.foreignCurrency
    private val localCurrency: LiveData<Currency> = currencyRepository.localCurrency

    val totalAmount = TotalAmountLiveData(billItemsWithPersons, localCurrency, foreignCurrency)

    val totalAmountPerPerson = TotalAmountPerPersonLiveData(persons, billItemsWithPersons, localCurrency, foreignCurrency)

    fun getOrGenerateBillItem(billItemId: Int?): LiveData<BillItem> {
        if (billItemId != null) {
            return billRepository.getBillItem(billItemId)
        }

        return MutableLiveData(BillItem("", 0.0, 1))
    }

    fun deleteBillItem(billItem: BillItem) {
        return billRepository.deleteBillItem(billItem)
    }


    fun getPersonsForBillItem(billItemId: Int?): MediatorLiveData<List<Pair<Person, Boolean>>> {
        val billPersons = if (billItemId != null) {billRepository.getPersonsForBillItem(billItemId)} else {
            MutableLiveData(emptyList())}

        return PersonsForBillItemLiveData(persons, billPersons)
    }

    fun saveBillItemWithPersons(billItem: BillItem?, persons: List<Pair<Person, Boolean>>) {
        if (billItem != null) {
            return billRepository.saveBillItemWithPersons(billItem, persons)
        }
        else {
            // TODO error handling
        }
    }

    fun formatPrice(amount: Double): String {
        return FormatUtils.formatPrice(amount, localCurrency.value, foreignCurrency.value)
    }

}
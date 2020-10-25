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

    fun addBillItem(billItem: BillItem): Int {
        return billRepository.addBillItem(billItem)
    }

    fun updateBillItem(billItem: BillItem) {
        return billRepository.updateBillItem(billItem)
    }

    fun getBillItem(billItemId: Int): LiveData<BillItem> {
        return billRepository.getBillItem(billItemId)
    }

    fun deleteBillItem(billItem: BillItem) {
        return billRepository.deleteBillItem(billItem)
    }


    fun getPersonsForBillItem(billItemId: Int): MediatorLiveData<Pair<List<Person>, List<Person>>> {
        val billPerson = billRepository.getPersonsForBillItem(billItemId)
        return MediatorLiveData<Pair<List<Person>, List<Person>>>().apply {
            addSource(persons) {
                value = Pair(it ?: emptyList(), billPerson.value ?: emptyList())
            }

            addSource(billPerson) {
                value = Pair(persons.value ?: emptyList(), it ?: emptyList())
            }
        }
    }

    fun addPersonToBillItem(billItem: BillItem, person: Person) {
        return billRepository.addPersonToBillItem(billItem, person)
    }

    fun removePersonFromBillItem(billItem: BillItem, person: Person) {
        return billRepository.removePersonFromBillItem(billItem, person)
    }

    fun updatePerson(person: Person) {
        return billRepository.updatePerson(person)
    }

    fun formatPrice(amount: Double): String {
        return FormatUtils.formatPrice(amount, localCurrency.value, foreignCurrency.value)
    }
}
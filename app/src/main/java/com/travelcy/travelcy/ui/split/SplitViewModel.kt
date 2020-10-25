package com.travelcy.travelcy.ui.split

import android.util.Log
import androidx.lifecycle.*
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.BillItemWithPersons
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Person
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.utils.FormatUtils

class SplitViewModel(private val billRepository: BillRepository, private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val billWithItems = billRepository.billWithItems

    val billItemsWithPersons = Transformations.map(billWithItems) {
        it?.items ?: emptyList()
    }

    val persons = Transformations.map(billWithItems) {
        it?.persons ?: emptyList()
    }

    private val foreignCurrency: LiveData<Currency> = currencyRepository.foreignCurrency
    private val localCurrency: LiveData<Currency> = currencyRepository.localCurrency

    val totalAmount: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        fun recalculateTotal(billItems: List<BillItemWithPersons>?, currency: Currency?) {
            val localAmount = billItems?.sumByDouble { it.billItem.amount * it.billItem.quantity } ?: 0.0

            value = formatPrice(localAmount, localCurrency.value, currency)
        }

        addSource(foreignCurrency) {
            recalculateTotal(billItemsWithPersons.value, it)
        }

        addSource(billItemsWithPersons) {
            recalculateTotal(it, foreignCurrency.value)
        }
    }

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

    fun formatPrice(amount: Double): String {
        return formatPrice(amount, localCurrency.value, foreignCurrency.value)
    }

    private fun formatPrice(amount: Double, local: Currency?, foreign: Currency?): String {
        var foreignAmount: Double? = null
        if (foreign != null) {
            foreignAmount = foreign.exchangeRate * amount
        }

        val formattedLocalAmount = FormatUtils.formatCurrency(amount, local?.id)
        val formattedForeignAmount = if (foreignAmount != null) {" / " + FormatUtils.formatCurrency(foreignAmount, foreign?.id)} else {""}

        return "$formattedLocalAmount$formattedForeignAmount"
    }
}
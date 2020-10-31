package com.travelcy.travelcy.ui.split

import android.util.Log
import androidx.lifecycle.*
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Person
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.utils.FormatUtils

class SplitViewModel(private val billRepository: BillRepository, private val currencyRepository: CurrencyRepository) : ViewModel() {
    val bill = billRepository.bill
    val billItemsWithPersons = billRepository.billItems
    val persons = billRepository.persons
    val defaultPerson = billRepository.defaultPerson

    private val foreignCurrency: LiveData<Currency> = currencyRepository.foreignCurrency
    private val localCurrency: LiveData<Currency> = currencyRepository.localCurrency

    private val foreignTotal = Transformations.map(billItemsWithPersons) {
        it?.sumByDouble { billItemWithPersons ->  billItemWithPersons.billItem.amount * billItemWithPersons.billItem.quantity } ?: 0.0
    }

    val billTipPercentage = Transformations.map(bill) {
        it?.tipPercentage
    }

    val billTipAmount =  Transformations.map(bill) {
        it?.tipAmount
    }

    val taxPercentage = Transformations.map(bill) {
        it?.taxPercentage
    }

    val taxTotal: LiveData<Double> = PercentageLiveData(foreignTotal, taxPercentage)
    val taxFormatted = FormatPriceLiveData(taxTotal, localCurrency, foreignCurrency)
    private val priceWithTax = LiveDataSum(foreignTotal, taxTotal)

    private val tipPercentage = TipPercentageLiveData(priceWithTax, billTipPercentage, billTipAmount)

    val tipTotal: LiveData<Double> = PercentageLiveData(priceWithTax, tipPercentage)
    val tipFormatted = FormatPriceLiveData(tipTotal, localCurrency, foreignCurrency)

    val totalAmount = TotalAmountLiveData(foreignTotal, taxTotal, tipTotal)

    val totalAmountFormatted = FormatPriceLiveData(totalAmount, localCurrency, foreignCurrency)

    val totalAmountPerPerson = TotalAmountPerPersonLiveData(persons, billItemsWithPersons, tipPercentage, taxPercentage, localCurrency, foreignCurrency)

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

    fun updateBudget(amount: String) {
        if (defaultPerson.value != null) {
            defaultPerson.value!!.budget = amount.toDouble()
            billRepository.updatePerson(defaultPerson.value!!)
        }
    }

    fun setTipAmount(amount: Double?) {
        billRepository.setTipAmount(amount)
    }

    fun setTipPercentage(percentage: Double?) {
        billRepository.setTipPercentage(percentage)
    }

    fun setTaxPercentage(percentage: Double) {
        billRepository.setTaxPercentage(percentage)
    }
}
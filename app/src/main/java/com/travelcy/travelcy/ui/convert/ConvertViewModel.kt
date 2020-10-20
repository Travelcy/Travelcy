package com.travelcy.travelcy.ui.convert

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.currency.CurrencyWebService

class ConvertViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {
    var toIndex = 0

    val localCurrency = currencyRepository.getLocalCurrency()
    val foreignCurrency = currencyRepository.getForeignCurrency()
    val currencies = currencyRepository.getCurrencies()

    val toAmount:MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val fromAmount:MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    fun switch(){
        // TODO: Switch currencies
    }

    fun listFromCurrencies(): MutableList<String> {
        val currencyNames = mutableListOf<String>()
        for (curr in currencies.value!!){
            currencyNames.add(curr.name)
        }
        return currencyNames
    }

    fun listToCurrencies(): MutableList<String> {
        val currencyNames = mutableListOf<String>()
        for (curr in currencies.value!!){
            currencyNames.add(curr.name)
        }
        return currencyNames
    }

    fun setLocalCurrency(position:Int) {
        currencyRepository.changeLocalCurrency(currencies.value!![position])
        Log.d("LOCALCURRENCY", localCurrency.value.toString())
    }

    fun setForeignCurrency(position:Int) {
        toIndex = position
        currencyRepository.changeForeignCurrency(currencies.value!![position])
        Log.d("FOREIGNCURRENCY", foreignCurrency.value.toString())
    }

    fun updateToAmount() {
        val fc = foreignCurrency.value!!.exchangeRate
        val fa = fromAmount.value!!
        toAmount.value = fc * fa
    }

    fun updateFromAmount(amount:Double) {
        fromAmount.value = amount
    }
}

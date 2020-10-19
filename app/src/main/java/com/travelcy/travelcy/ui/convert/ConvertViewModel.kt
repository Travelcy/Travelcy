package com.travelcy.travelcy.ui.convert

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.currency.CurrencyWebService

class ConvertViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {
    val localCurrency = currencyRepository.getLocalCurrency()
    val currencies = currencyRepository.getCurrencies()
    val foreignCurrency = currencyRepository.getForeignCurrency()

    fun switch(){
        // listToCurrenceies()
    }

    fun listFromCurrencies(): MutableList<String> {
        var currencyNames = mutableListOf<String>()
        for (curr in currencies.value!!){
            currencyNames.add(curr.name)
        }
        return currencyNames
    }

    fun listToCurrencies(): MutableList<String> {
        var currencyNames = mutableListOf<String>()
        for (curr in currencies.value!!){
            currencyNames.add(curr.name)
        }
        return currencyNames
    }

}

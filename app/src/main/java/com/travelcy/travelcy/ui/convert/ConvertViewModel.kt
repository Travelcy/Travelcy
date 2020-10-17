package com.travelcy.travelcy.ui.convert

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.model.Currency

class ConvertViewModel : ViewModel() {
    // Mock data
    val USD = Currency("USD",  listOf<Pair<String, Double>>(Pair("EUR", 1.123), Pair("ISK", 120.222)))
    val EUR = Currency("EUR",  listOf<Pair<String, Double>>(Pair("USD", 0.877), Pair("ISK", 140.983)))
    val ISK = Currency("ISK",  listOf<Pair<String, Double>>(Pair("EUR", 0.0014), Pair("USD", 0.0012)))

    val currencies = listOf<Currency>(USD,EUR,ISK)


    // not mock data
    var fromCurr = 0
    var toCurr = 0



    val from_currencies = MutableLiveData<MutableList<String>>(listFromCurrencies())
  //  val to_currencies = MutableLiveData<MutableList<String>>(listToCurrencies())



    fun switch(){
        // listToCurrenceies()
    }


    fun setSelectedFromCurr(index: Int){
        fromCurr = index
        setSelectedToCurr(toCurr)
    }


    fun setSelectedToCurr(index: Int){
        toCurr = index
    }


    fun listFromCurrencies(): MutableList<String> {
        var currencyNames = mutableListOf<String>()
        for (curr in currencies){
            currencyNames.add(curr.name)
        }
        return currencyNames
    }


    fun listToCurrencies(): MutableList<String> {
        var currencyNames = mutableListOf<String>()
        for (curr in currencies[fromCurr].rates){
            currencyNames.add(curr.first)
        }
        return currencyNames
    }

}

package com.travelcy.travelcy.ui.convert

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.model.Currency

class ConvertViewModel : ViewModel() {
    val USD = Currency("USD",  listOf<Pair<String, Double>>(Pair("EUR", 1.123), Pair("ISK", 120.222)))
    val EUR = Currency("EUR",  listOf<Pair<String, Double>>(Pair("USD", 0.877), Pair("ISK", 140.983)))
    val ISK = Currency("ISK",  listOf<Pair<String, Double>>(Pair("EUR", 0.0014), Pair("USD", 0.0012)))

    //val currencies = listOf<Currency>(USD,EUR,ISK)

    val current_currencies = MutableLiveData<List<Currency>>(listOf<Currency>(USD,EUR,ISK))


/*
    fun listCurrencies(): MutableList<String> {
        var currencyNames = mutableListOf<String>()
        for (curr in currencies){
            currencyNames.add(curr.name)
        }
        return currencyNames
    }
*/
}

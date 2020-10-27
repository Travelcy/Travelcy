package com.travelcy.travelcy.ui.convert

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.currency.LocalCurrencyIdsLiveData
import com.travelcy.travelcy.services.location.LocationRepository
import java.math.RoundingMode
import java.text.DecimalFormat


class ConvertViewModel(private val currencyRepository: CurrencyRepository, private val locationRepository: LocationRepository) : ViewModel() {
    var toIndex = 0
    val localCurrency = currencyRepository.localCurrency
    val foreignCurrency = currencyRepository.foreignCurrency
    private val currencies = currencyRepository.currencies
    val networkConnected = currencyRepository.networkConnected

    val currencyIds = Transformations.map(currencies) {
        it.sortedBy { currency -> currency.sort }
            .filter { currency -> currency.enabled }
            .map { currency -> currency.id }
    }

    val localCurrencyIds = LocalCurrencyIdsLiveData(currencyIds, localCurrency, networkConnected)

    val fromAmount = MutableLiveData<Double>(1.0)

    val toAmount: MediatorLiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(foreignCurrency) {
            value = (it?.exchangeRate ?: 0.0) * (fromAmount.value ?: 0.0)
        }

        addSource(fromAmount) {
            value = it * (foreignCurrency.value?.exchangeRate ?: 0.0)
        }
    }

    fun switch(){
        currencyRepository.switchCurrencies()
    }

    fun setLocalCurrency(position:Int) {
        currencyRepository.changeLocalCurrency(currencyIds.value!![position])
    }

    fun setForeignCurrency(position:Int) {
        toIndex = position
        currencyRepository.changeForeignCurrency(currencyIds.value!![position])
    }

    fun formatAmount(amount: Double): String {
        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.CEILING
        return decimalFormat.format(amount)
    }

    fun updateFromAmount(amount: String) {
        if (formatAmount(fromAmount.value ?: 0.0) != amount) {
            fromAmount.value = amount.toDouble()
        }
    }

    fun positionOfLocalCurrency(): Int {
        return currencyIds.value?.indexOf(localCurrency.value?.id) ?: -1
    }

    fun positionOfForeignCurrency(): Int {
        return currencyIds.value?.indexOf(foreignCurrency.value?.id) ?: -1
    }

    fun updateCurrencyBasedOnLocation() {
        locationRepository.updateForeignCurrencyFromLocation()
    }
}

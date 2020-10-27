package com.travelcy.travelcy.ui.convert

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.travelcy.travelcy.services.currency.CurrencyRepository
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

    val foreignAmount = MutableLiveData<Double>(1.0)

    val localAmount = LocalAmountLiveData(foreignAmount, foreignCurrency)

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

    fun updateForeignAmount(amount: String) {
        if (formatAmount(foreignAmount.value ?: 0.0) != amount) {
            foreignAmount.value = amount.toDouble()
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

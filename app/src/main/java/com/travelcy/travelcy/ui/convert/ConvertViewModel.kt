package com.travelcy.travelcy.ui.convert

import android.util.Log
import androidx.lifecycle.MediatorLiveData
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

    val currencyIds = Transformations.map(currencies) {
        it.map { currency ->
            currency.id
        }
    }

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
        currencyRepository.changeLocalCurrency(currencies.value!![position])
        Log.d("LOCALCURRENCY", localCurrency.value.toString())
    }

    fun setForeignCurrency(position:Int) {
        toIndex = position
        currencyRepository.changeForeignCurrency(currencies.value!![position])
        Log.d("FOREIGNCURRENCY", foreignCurrency.value.toString())
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

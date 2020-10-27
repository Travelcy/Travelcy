package com.travelcy.travelcy.services.currency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.travelcy.travelcy.MainActivity
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Settings
import java.lang.Exception
import java.util.concurrent.Executor

class CurrencyRepository (
    private val currencyWebService: CurrencyWebService,
    private val currencyDao: CurrencyDao,
    private val settingsDao: SettingsDao,
    private val executor: Executor
) {
    var loadingCurrencies = true
    val networkConnected = MutableLiveData(false)
    val settings: LiveData<Settings> = settingsDao.getSettings()
    val currenciesLoaded = MutableLiveData(false)

    private val _localCurrencyCode: LiveData<String> = Transformations.map(settings) {
        Log.d(TAG, "Settings changed, local currency code changed to ${it?.localCurrencyCode ?: "undefined"}")
        it?.localCurrencyCode
    }

    private val localCurrencyCode = Transformations.distinctUntilChanged(_localCurrencyCode)

    private val _foreignCurrencyCode: LiveData<String> = Transformations.map(settings) {
        Log.d(TAG, "Settings changed, foreign currency code changed to ${it?.foreignCurrencyCode ?: "undefined"}")
        it?.foreignCurrencyCode
    }

    private val foreignCurrencyCode = Transformations.distinctUntilChanged(_foreignCurrencyCode)

    val currencies = Transformations.switchMap<Boolean, List<Currency>>(currenciesLoaded) {currenciesLoaded ->
        if(currenciesLoaded) {currencyDao.loadCurrencies()} else {MutableLiveData()}
    }

    val localCurrency = CurrencyLiveData(localCurrencyCode, currencies)

    val foreignCurrency = CurrencyLiveData(foreignCurrencyCode, currencies)

    init {
        currenciesLoaded.observeForever {
            if (it == true) {
                MainActivity.instance?.onAppLoaded()
            }
        }

        executor.execute {
            if (currencyDao.hasCurrencies()) {
                currenciesLoaded.postValue(true)
            }
        }

        localCurrencyCode.observeForever {
            if (it != null) {
                updateCurrencies(it)
            }
        }
    }

    private fun updateSettings(localCurrencyId: String, foreignCurrencyId: String) {
        Log.d(TAG, "updateSettings(localCurrencyId: $localCurrencyId, foreignCurrencyId: $foreignCurrencyId)")

        executor.execute {
            settingsDao.updateSettings(Settings(localCurrencyId, foreignCurrencyId))
        }
    }

    fun switchCurrencies() {
        val foreignCurrencyCodeValue = foreignCurrencyCode.value
        val localCurrencyCodeValue = localCurrencyCode.value

        if (foreignCurrencyCodeValue != null && localCurrencyCodeValue != null) {
            Log.d(TAG, "Switching currencies from ${foreignCurrencyCodeValue} to ${localCurrencyCodeValue}")
            updateSettings(foreignCurrencyCodeValue, localCurrencyCodeValue)
        }
        else {
            Log.e(TAG, "Could not switch currencies, either foreignCurrency or localCurrency were not defined")
        }
    }

    fun changeLocalCurrency(currencyCode: String) {
        Log.d(TAG, "Changing local currency to ${currencyCode}")

        if (currencyCode == localCurrency.value?.id) {
            Log.d(TAG, "Local currency already set to value")
            return
        }

        // If the foreign currency is the same as the local currency being set we turn them around
        if (foreignCurrency.value?.id == currencyCode) {
            switchCurrencies()
        }
        else {
            executor.execute {
                settingsDao.updateLocalCurrencyCode(currencyCode)
            }
        }
    }

    fun changeForeignCurrency(currencyCode: String) {
        Log.d(TAG, "Changing foreign currency to ${currencyCode}")

        if (currencyCode == foreignCurrency.value?.id) {
            Log.d(TAG, "Foreign currency already set to value")
            return
        }

        // If the foreign currency is the same as the local currency we turn them around
        if (localCurrency.value?.id == currencyCode) {
            switchCurrencies()
        }
        else {
            executor.execute {
                settingsDao.updateForeignCurrencyCode(currencyCode)
            }
        }
    }

    private fun updateCurrencies(currencyBase: String) {
        Log.d(TAG,"Update currencies (currencyBase: $currencyBase)")

        if (loadingCurrencies)

        loadingCurrencies = true

        // Runs in background thread
        executor.execute {
            try {
                val response = currencyWebService.getCurrencies(currencyBase).execute()

                if (response.isSuccessful) {
                    val currencyWebServiceResponse = response.body()

                    var i = 0
                    currencyWebServiceResponse?.rates?.forEach {
                        val currency = java.util.Currency.getInstance(it.key)
                        val currencyModel: Currency? = currencyDao.getRawCurrency(it.key)

                        if (currencyModel != null) {
                            currencyModel.name = currency.displayName
                            currencyModel.exchangeRate = it.value
                            currencyDao.updateCurrency(currencyModel)
                        }
                        else {
                            currencyDao.insertCurrency(Currency(
                                it.key,
                                currency.displayName,
                                it.value,
                                true,
                                i++
                            ))
                        }
                    }

                    val currencyCodes = currencyWebServiceResponse?.rates?.map { it.key }

                    if (currencyCodes != null && currencyCodes.isNotEmpty()) {
                        currencyDao.deleteOtherCurrencies(currencyCodes)
                    }

                    currenciesLoaded.postValue(true)
                }
            }
            catch (exception: Exception) {
                Log.e(TAG, "Failed to fetch currencies", exception)
            }

            loadingCurrencies = false
        }
    }

    fun setNetworkConnected(isConnected: Boolean) {
        Log.d(TAG, "setNetworkConected(isConnected: $isConnected)")
        networkConnected.postValue(isConnected)

        if (isConnected && currenciesLoaded.value != true && !loadingCurrencies && localCurrencyCode.value != null) {
            Log.d(TAG, "updating currencies after network change")
            updateCurrencies(localCurrencyCode.value as String)
        }
        else {
            Log.d(TAG, "Not updateint after network change")

            Log.d(TAG, "CurrenciesLoaded ${currenciesLoaded.value}")
            Log.d(TAG, "Loading currencies $loadingCurrencies")
            Log.d(TAG, "Local currency code ${localCurrencyCode.value}")
        }
    }

    fun updateCurrency(currency: Currency) {
        executor.execute {
            currencyDao.updateCurrency(currency)
        }
    }

    fun updateCurrencies(currencies: List<Currency>) {
        executor.execute {
            currencyDao.updateCurrencies(currencies)
        }
    }

    companion object {
        private const val TAG = "CurrencyRepository"
    }
}
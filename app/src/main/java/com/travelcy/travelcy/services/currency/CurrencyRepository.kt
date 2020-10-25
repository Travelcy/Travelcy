package com.travelcy.travelcy.services.currency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Settings
import java.util.concurrent.Executor

class CurrencyRepository (
    private val currencyWebService: CurrencyWebService,
    private val currencyDao: CurrencyDao,
    private val settingsDao: SettingsDao,
    private val executor: Executor
) {
    val settings: LiveData<Settings> = settingsDao.getSettings()
    val currencies = currencyDao.loadCurrencies()

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


    val localCurrency = Transformations.switchMap(localCurrencyCode) {
        if (it != null) { currencyDao.getCurrency(it) } else { null }
    }

    val foreignCurrency = Transformations.switchMap(foreignCurrencyCode) {
        if (it != null) { currencyDao.getCurrency(it) } else { null }
    }

    init {
        localCurrencyCode.observeForever {
            if (it != null) {
                updateCurrencies(it)
            }
        }

        executor.execute {
            if (!settingsDao.hasSettings()) {
                Log.d(TAG, "No settings in database, setting up initial settings")
                // Setup initial settings
                updateSettings("ISK", "USD")
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
        // Runs in background thread
        executor.execute {
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
                        Log.d(TAG, "Updating currency: ${it.key}")
                        currencyDao.updateCurrency(currencyModel)
                    }
                    else {
                        Log.d(TAG, "Inserting currency: ${it.key}")
                        currencyDao.insertCurrency(Currency(
                            it.key,
                            currency.displayName,
                            it.value,
                            true,
                            i++
                        ))
                    }
                }
            }
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
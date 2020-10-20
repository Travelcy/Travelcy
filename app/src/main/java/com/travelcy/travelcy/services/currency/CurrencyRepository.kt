package com.travelcy.travelcy.services.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.database.entity.SettingsWithCurrencies
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Settings
import java.util.concurrent.Executor

class CurrencyRepository (
    private val currencyWebService: CurrencyWebService,
    private val currencyDao: CurrencyDao,
    private val settingsDao: SettingsDao,
    private val executor: Executor
) {
    val settings: LiveData<SettingsWithCurrencies> = settingsDao.getSettings()

    init {
        updateCurrencies()
    }

    private fun initSettings(currencies: List<Currency>) {
        if (!settingsDao.hasSettings() && currencies.isNotEmpty()) {
            val iskCurrency = currencies.find { currency -> currency.id == "ISK" }
            val usdCurrency = currencies.find { currency -> currency.id == "USD" }

            if (iskCurrency != null && usdCurrency != null) {
                executor.execute {
                    settingsDao.updateSettings(Settings(iskCurrency.id, usdCurrency.id))
                }
            }
        }
    }

    fun getLocalCurrency(): LiveData<Currency> {
       return Transformations.map(settings) {
           it?.localCurrency
       }
    }

    fun getForeignCurrency(): LiveData<Currency> {
        return Transformations.map(settings) {
            it?.foreignCurrency
        }
    }

    fun changeLocalCurrency(currency: Currency) {
        if (currency.id == settings.value?.localCurrency?.id) {
            return
        }
        // If the foreign currency is the same as the local currency we turn them around
        var foreignCurrencyId = settings.value?.foreignCurrency?.id

        if (foreignCurrencyId == currency.id) {
            foreignCurrencyId = settings.value?.localCurrency?.id
        }

        executor.execute {
            settingsDao.updateSettings(Settings(currency.id, foreignCurrencyId))
        }
    }

    fun changeForeignCurrency(currency: Currency) {
        if (currency.id == settings.value?.foreignCurrency?.id) {
            return
        }
        // If the foreign currency is the same as the local currency we turn them around
        var localCurrencyId = settings.value?.localCurrency?.id

        if (localCurrencyId == currency.id) {
            localCurrencyId = settings.value?.foreignCurrency?.id
        }

        executor.execute {
            settingsDao.updateSettings(Settings(localCurrencyId, currency.id))
        }
    }

    fun getCurrencies(): LiveData<List<Currency>> {
        updateCurrencies()
        val data = currencyDao.loadCurrencies()
        return data
    }

    fun updateCurrencies() {
        // Runs in background thread
        executor.execute {
            if (!currencyDao.hasCurrencies()) {
                val response = currencyWebService.getCurrencies("ISK").execute()

                if (response.isSuccessful) {
                    val currencyWebServiceResponse = response.body()

                    val currencies = mutableListOf<Currency>()
                    currencyWebServiceResponse?.rates?.forEach {
                        val currency = java.util.Currency.getInstance(it.key)

                        currencies.add(
                            Currency(
                                it.key,
                                currency.displayName,
                                it.value
                            )
                        )
                    }

                    currencyDao.insertAll(currencies)
                    initSettings(currencies)
                }
            }
        }.run {  }
    }

}
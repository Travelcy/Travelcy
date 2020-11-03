package com.travelcy.travelcy.services.settings

import com.travelcy.travelcy.database.dao.SettingsDao
import java.util.concurrent.Executor

class SettingsRepository(
    private val settingsDao: SettingsDao,
    private val executor: Executor
) {
    val exchangeRatesLastUpdated = settingsDao.getExchangeRatesLastUpdated()
    val autoUpdateExchangeRates = settingsDao.getAutoUpdateExchangeRates()

    fun updateAutoUpdateExchangeRates(autoUpdate: Boolean) {
        executor.execute {
            settingsDao.updateAutoUpdateExchangeRates(autoUpdate)
        }
    }
}
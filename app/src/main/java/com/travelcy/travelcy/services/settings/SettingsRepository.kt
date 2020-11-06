package com.travelcy.travelcy.services.settings

import androidx.lifecycle.Transformations
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.workers.ExchangeRateUpdateWorker
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class SettingsRepository(
    private val settingsDao: SettingsDao,
    private val workManager: WorkManager,
    private val executor: Executor
) {
    val exchangeRatesLastUpdated = settingsDao.getExchangeRatesLastUpdated()
    val autoUpdateExchangeRates = Transformations.distinctUntilChanged(settingsDao.getAutoUpdateExchangeRates())

    init {
        autoUpdateExchangeRates.observeForever {
            workManager.cancelAllWork()

            if (it) {
                val exchangeRateUpdateRequest = PeriodicWorkRequestBuilder<ExchangeRateUpdateWorker>(1, TimeUnit.DAYS).build()

                workManager.enqueueUniquePeriodicWork(ExchangeRateUpdateWorker.WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, exchangeRateUpdateRequest);
            }
        }
    }

    fun updateAutoUpdateExchangeRates(autoUpdate: Boolean) {
        executor.execute {
            settingsDao.updateAutoUpdateExchangeRates(autoUpdate)
        }
    }
}
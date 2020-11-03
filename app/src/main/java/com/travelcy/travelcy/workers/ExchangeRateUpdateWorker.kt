package com.travelcy.travelcy.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.travelcy.travelcy.MainApplication
import com.travelcy.travelcy.database.TravelcyDatabase
import java.lang.Exception

class ExchangeRateUpdateWorker (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
            val application = applicationContext as MainApplication
            val database = TravelcyDatabase.getInstance(application)
            val settingsDao = database.settingsDao()

            application.getCurrencyRepository().updateCurrencies(settingsDao.getSettingsRaw().localCurrencyCode!!)

            return Result.success()
        }
        catch (e: Exception){
            return Result.failure()
        }
    }

    companion object {
        val WORK_NAME = "EXCHANGE_RATE_UPDATER"
    }
}
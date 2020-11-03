package com.travelcy.travelcy.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.travelcy.travelcy.MainApplication
import java.lang.Exception

class ExchangeRateUpdateWorker (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
            val application = applicationContext as MainApplication

            application.getCurrencyRepository().refreshCurrencies()

            return Result.success()
        }
        catch (e: Exception){}

        return Result.failure()
    }

    companion object {
        val WORK_NAME = "EXCHANGE_RATE_UPDATER"
    }
}
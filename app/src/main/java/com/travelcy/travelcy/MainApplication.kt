package com.travelcy.travelcy

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bugsnag.android.Bugsnag
import com.travelcy.travelcy.database.TravelcyDatabase
import com.travelcy.travelcy.services.bill.BillRepository
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.currency.CurrencyWebService
import com.travelcy.travelcy.services.settings.SettingsRepository
import com.travelcy.travelcy.workers.ExchangeRateUpdateWorker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainApplication : Application() {
    private var currencyWebService: CurrencyWebService? = null

    private var currencyRepository: CurrencyRepository? = null

    private var billRepository: BillRepository? = null

    private var settingsRepository: SettingsRepository? = null

    private lateinit var workManager: WorkManager;

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)

    var appLoaded = false

    override fun onCreate() {
        super.onCreate()

        Bugsnag.start(this)

        workManager = WorkManager.getInstance(this)
    }

    fun getCurrencyWebService(): CurrencyWebService {
        if (currencyWebService == null) {
            currencyWebService = Retrofit.Builder()
                .baseUrl(CURRENCY_WEBSERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CurrencyWebService::class.java)
        }

        return currencyWebService as CurrencyWebService
    }

    fun getCurrencyRepository(): CurrencyRepository {
        if (currencyRepository == null) {
            val database = TravelcyDatabase.getInstance(this)
            currencyRepository = CurrencyRepository(getCurrencyWebService(), database.currencyDao(), database.settingsDao(), executorService)
        }

        return currencyRepository as CurrencyRepository
    }

    fun getBillRepository(): BillRepository {
        if (billRepository == null) {
            val database = TravelcyDatabase.getInstance(this)
            billRepository = BillRepository(database.billDao(), executorService)
        }

        return billRepository as BillRepository
    }

    fun getSettingsRepository(): SettingsRepository {
        if (settingsRepository == null) {
            val database = TravelcyDatabase.getInstance(this)
            settingsRepository = SettingsRepository(database.settingsDao(), workManager, executorService)
        }

        return settingsRepository as SettingsRepository
    }

    companion object {
        const val CURRENCY_WEBSERVICE_BASE_URL = "https://api.exchangeratesapi.io/"
    }
}
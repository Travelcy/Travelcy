package com.travelcy.travelcy

import android.app.Application
import android.content.Context
import com.bugsnag.android.Bugsnag
import com.travelcy.travelcy.database.TravelcyDatabase
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.currency.CurrencyWebService
import com.travelcy.travelcy.services.location.LocationRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainApplication : Application() {
    private var currencyWebService: CurrencyWebService? = null

    private var currencyRepository: CurrencyRepository? = null

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)

    private var locationRepository:LocationRepository? = null

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Bugsnag.start(this)
        val context: Context = applicationContext()
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

    fun getLocationRepository(): LocationRepository {
        if (locationRepository == null) {
            val database = TravelcyDatabase.getInstance(this)
            locationRepository = LocationRepository(database.locationDao(), executorService)
        }

        return locationRepository as LocationRepository
    }

    companion object {
        const val CURRENCY_WEBSERVICE_BASE_URL = "https://api.exchangeratesapi.io/"
        private var instance: MainApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
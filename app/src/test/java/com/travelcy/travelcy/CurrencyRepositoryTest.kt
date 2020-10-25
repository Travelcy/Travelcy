package com.travelcy.travelcy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Settings
import com.travelcy.travelcy.services.currency.CurrencyRepository
import com.travelcy.travelcy.services.currency.CurrencyWebService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.*
import org.junit.rules.TestRule
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class CurrencyDaoMock: CurrencyDao {
    private val currencies = arrayOf(
        Currency("ISK", "Icelandic Króna", 1.0),
        Currency("CAD", "Canadian dollar", 0.009546683),
        Currency("DKK", "Danish krona", 0.0457137592),
        Currency("SEK", "Swedish krona", 0.064004914),
        Currency("EUR", "Euros", 0.0061425061),
        Currency("NOK", "Norwegian crona", 0.0667217445),
        Currency("USD", "US dollar", 0.007245086),
        Currency("AUD", "Australian dollar", 0.0100767813)
    ).toList()

    override fun getRawCurrency(currencyCode: String): Currency? {
        return currencies.find {it.id == currencyCode}
    }

    override fun getCurrency(currencyCode: String): LiveData<Currency> {
        return MutableLiveData<Currency>(getRawCurrency(currencyCode))
    }

    override fun hasCurrencies(): Boolean {
        return true
    }

    override fun loadCurrencies(): LiveData<List<Currency>> {
        return MutableLiveData<List<Currency>>(currencies)
    }

    override fun insertCurrency(currency: Currency) {
        // Do nothing, were not testing the dao here
    }

    override fun updateCurrency(currency: Currency) {
        // Do nothing, were not testing the dao here
    }

    override fun updateCurrencies(currencies: List<Currency>) {
        // Do nothing, were not testing the dao here
    }

    override fun deleteOtherCurrencies(currencyCodes: List<String>) {
        // Do nothing, were not testing the dao here
    }
}

class SettingsDaoMock: SettingsDao {
    private val _settings: MutableLiveData<Settings> = MutableLiveData()

    init {
        updateSettings(Settings("ISK", "USD"))
    }

    override fun hasSettings(): Boolean {
        return true
    }

    override fun getSettingsRaw(): Settings {
        return _settings.value!!
    }

    private fun createCurrencyFromId(id: String?): Currency? {
        return when (id) {
            "ISK" -> {
                Currency(id, "Icelandic Króna", 1.0)
            }
            "USD" -> {
                Currency(id, "US dollar", 0.007245086)
            }
            else -> {
                null
            }
        }
    }

    override fun updateSettings(settings: Settings) {
        _settings.postValue(settings)
    }

    override fun updateLocalCurrencyCode(localCurrencyCode: String) {
        _settings.postValue(Settings(localCurrencyCode, _settings.value?.foreignCurrencyCode))
    }

    override fun updateForeignCurrencyCode(foreignCurrencyCode: String) {
        _settings.postValue(Settings(_settings.value?.localCurrencyCode, foreignCurrencyCode))
    }

    override fun getSettings(): LiveData<Settings> {
        return _settings
    }

}

class SynchronousExecutor : Executor {
    override fun execute(runnable: Runnable) {
        runnable.run()
    }
}

class CurrencyRepositoryTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val executorService: SynchronousExecutor = SynchronousExecutor()

    // Mock server to mock the api
    private var mockWebServer = MockWebServer()

    private lateinit var currencyWebService: CurrencyWebService

    private lateinit var currencyRepository: CurrencyRepository

    private lateinit var response: MockResponse

    @Before
    fun setup() {
        // Gson converts JSON responses to java classes
        val gson = GsonBuilder().setLenient().create()

        mockWebServer.start()

        // Create a retrofit instance with the mock server as url and Gson for convertion
        val retrofit = Retrofit
            .Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val ratesJson = JSONObject()
            .put("CAD", 0.009546683)
            .put("ISK", 1.0)
            .put("DKK", 0.0457137592)
            .put("SEK", 0.064004914)
            .put("EUR", 0.0061425061)
            .put("NOK", 0.0667217445)
            .put("USD", 0.007245086)
            .put("AUD", 0.0100767813)

        val jsonResponse = JSONObject()
            .put("rates", ratesJson)
            .put("base", "ISK")
            .put("date", "2020-10-09")

        response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json")
            .setBody(jsonResponse.toString())

        mockWebServer.enqueue(response)

        currencyWebService = retrofit.create(CurrencyWebService::class.java)

        currencyRepository = CurrencyRepository(currencyWebService, CurrencyDaoMock(), SettingsDaoMock(), executorService)
    }

    @Test
    fun testGetSettings() {
        val localCurrency = currencyRepository.localCurrency.blockingObserve()
        val foreignCurrency = currencyRepository.foreignCurrency.blockingObserve()

        Assert.assertEquals("ISK", localCurrency?.id)
        Assert.assertEquals("USD", foreignCurrency?.id)
    }

    @Test
    fun testChangeLocalCurrency() {
        val localCurrencyBefore = currencyRepository.localCurrency.blockingObserve()
        val foreignCurrencyBefore = currencyRepository.foreignCurrency.blockingObserve()

        Assert.assertEquals("ISK", localCurrencyBefore?.id)
        Assert.assertEquals("USD", foreignCurrencyBefore?.id)

        mockWebServer.enqueue(response)

        currencyRepository.changeLocalCurrency(foreignCurrencyBefore!!.id)

        val localCurrency = currencyRepository.localCurrency.blockingObserve()
        val foreignCurrency = currencyRepository.foreignCurrency.blockingObserve()

        Assert.assertEquals("USD", localCurrency?.id)
        Assert.assertEquals("ISK", foreignCurrency?.id)
    }

    @Test
    fun testChangeForeignCurrency() {
        val localCurrencyBefore = currencyRepository.localCurrency.blockingObserve()
        val foreignCurrencyBefore = currencyRepository.foreignCurrency.blockingObserve()

        Assert.assertEquals("ISK", localCurrencyBefore?.id)
        Assert.assertEquals("USD", foreignCurrencyBefore?.id)

        mockWebServer.enqueue(response)

        currencyRepository.changeForeignCurrency(localCurrencyBefore!!.id)

        val localCurrency = currencyRepository.localCurrency.blockingObserve()
        val foreignCurrency = currencyRepository.foreignCurrency.blockingObserve()

        Assert.assertEquals("USD", localCurrency?.id)
        Assert.assertEquals("ISK", foreignCurrency?.id)
    }

    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> { t ->
            value = t
            latch.countDown()
        }

        observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)
        return value
    }
}
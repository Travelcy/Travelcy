package com.travelcy.travelcy

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.TravelcyDatabase
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Settings
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Currency as JavaCurrency
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TravelcyDatabaseTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var currencyDao: CurrencyDao
    private lateinit var settingsDao: SettingsDao
    private lateinit var travelcyDatabase: TravelcyDatabase
    private lateinit var currencies: Array<Currency>

    /**
     * Setup test
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create database instance for testing purposes
        travelcyDatabase = Room.inMemoryDatabaseBuilder(context, TravelcyDatabase::class.java).build()

        currencyDao = travelcyDatabase.currencyDao()
        settingsDao = travelcyDatabase.settingsDao()

        val iskCurrency = JavaCurrency.getInstance("ISK")
        val usdCurrency = JavaCurrency.getInstance("USD")

        // Currencies for testing purposes
        currencies = arrayOf(
            Currency(
                "ISK",
                iskCurrency.getDisplayName(Locale.getDefault()),
                1.0
            ),
            Currency(
                "USD",
                usdCurrency.getDisplayName(Locale.getDefault()),
                0.007245086
            )
        )

        // Insert currencies to database
        currencyDao.insertAll(currencies.asList())
    }

    /**
     * Clean up after test
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        travelcyDatabase.close()
    }

    /**
     * Test that settings will save and load
     */
    @Test
    fun testSettingsSaveLoad() {
        val settings  = Settings("ISK", "USD")

        settingsDao.updateSettings(settings)

        val retrievedSettings = settingsDao.getSettings().blockingObserve()

        Assert.assertNotNull(retrievedSettings)
    }

    /**
     * Test that settings relations work correctly
     */
    @Test
    fun testSettingsRelations() {
        val settings  = Settings("ISK", "USD")

        settingsDao.updateSettings(settings)

        val retrievedSettings = settingsDao.getSettings().blockingObserve()

        Assert.assertNotNull(retrievedSettings)

        Assert.assertEquals(retrievedSettings?.localCurrency?.id, "ISK")

        Assert.assertEquals(retrievedSettings?.foreignCurrency?.id, "USD")
    }


    /**
     * Test that adding new settings will not add a new settings object
     */
    @Test
    fun testSettingSingelton() {
        val settings  = Settings("ISK", "USD")

        Assert.assertEquals(settings.id, 1)

        val secondSettings = Settings("USD", "ISK")

        Assert.assertEquals(secondSettings.id, 1)

        settingsDao.updateSettings(settings)

        settingsDao.updateSettings(secondSettings)

        val retrievedSettings = settingsDao.getSettings().blockingObserve()

        Assert.assertNotNull(retrievedSettings)

        Assert.assertEquals(retrievedSettings?.localCurrency?.id, "USD")

        Assert.assertEquals(retrievedSettings?.foreignCurrency?.id, "ISK")
    }


    /**
     * Test that currencies will save and load
     */
    @Test
    fun testCurrencySaveAndLoad() {
        val retrievedCurrencies = currencyDao.loadCurrencies().blockingObserve()

        Assert.assertNotNull(retrievedCurrencies)

        retrievedCurrencies?.forEachIndexed { index, element ->
            Assert.assertEquals(element, currencies.get(index))
        }
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

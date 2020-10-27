package com.travelcy.travelcy

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.BillItemWithPersons
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Person
import com.travelcy.travelcy.services.currency.CurrencyLiveData
import com.travelcy.travelcy.services.currency.LocalCurrencyIdsLiveData
import com.travelcy.travelcy.ui.split.PersonsForBillItemLiveData
import com.travelcy.travelcy.ui.split.TotalAmountLiveData
import com.travelcy.travelcy.ui.split.TotalAmountPerPersonLiveData
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MediatorLiveDataTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
       Locale.setDefault(Locale.US)
    }

    @Test
    fun testTotalAmountLiveData() {
        val localCurrency = MutableLiveData(Currency("ISK", "Króna", 1.0))
        val foreignCurrency = MutableLiveData(Currency("USD", "Dollar", 0.01))

        val billItem1 = BillItem("Pizza", 1000.0, 2)

        val billItem2 = BillItem("Beer", 500.0, 4)

        val billItemWithPersons = MutableLiveData(listOf(BillItemWithPersons(billItem1, emptyList()), BillItemWithPersons(billItem2, emptyList())))

        val totalAmountLiveData = TotalAmountLiveData(billItemWithPersons, localCurrency, foreignCurrency).blockingObserve()

        Assert.assertEquals("ISK4,000 / $40", totalAmountLiveData)
    }

    @Test
    fun testTotalAmountPerPersonLiveData() {
        val localCurrency = MutableLiveData(Currency("ISK", "Króna", 1.0))
        val foreignCurrency = MutableLiveData(Currency("USD", "Dollar", 0.01))

        val sharedItem = BillItem("Pizza", 1000.0, 4)

        val singleItemPerson1 = BillItem("Beer", 500.0, 2)

        val singleItemPerson2 = BillItem("Juice", 500.0, 1)

        val person1 = Person("Test person 1")
        person1.id = 1

        val person2 = Person("Test person 2")
        person2.id = 2

        val persons = MutableLiveData(listOf(person1, person2))

        Assert.assertEquals(2, persons.value?.size)

        val billItem1 = BillItemWithPersons(sharedItem, listOf(person1, person2))

        val billItem2 = BillItemWithPersons(singleItemPerson1, listOf(person1))

        val billItem3 = BillItemWithPersons(singleItemPerson2, listOf(person2))

        val billItemWithPersons = MutableLiveData(listOf(billItem1, billItem2, billItem3))

        val totalAmountPerPersonLiveData = TotalAmountPerPersonLiveData(persons, billItemWithPersons, localCurrency, foreignCurrency).blockingObserve()

        Assert.assertNotNull(totalAmountPerPersonLiveData)

        totalAmountPerPersonLiveData?.forEachIndexed { index, (person, price) ->
            if (index == 0) {
                Assert.assertEquals("Test person 1", person.name)
                Assert.assertEquals("ISK3,000 / $30", price)
            } else if (index == 1) {
                Assert.assertEquals("Test person 2", person.name)
                Assert.assertEquals("ISK2,500 / $25", price)
            }
        }
    }

    @Test
    fun testPersonsForBillItemLiveData() {
        val person1 = Person("Test person 1")
        person1.id = 1

        val person2 = Person("Test person 2")
        person2.id = 2

        val person3 = Person("Test person 3")
        person3.id = 3

        val totalPersons = MutableLiveData(listOf(person1, person2, person3))

        val selectedPersons = MutableLiveData(listOf(person1, person2))

        val billItemPersons = PersonsForBillItemLiveData(totalPersons, selectedPersons).blockingObserve()

        Assert.assertNotNull(billItemPersons)

        billItemPersons?.forEachIndexed { index, (person, selected) ->
            if (index == 0) {
                Assert.assertEquals("Test person 1", person.name)
                Assert.assertEquals(true, selected)
            } else if (index == 1) {
                Assert.assertEquals("Test person 2", person.name)
                Assert.assertEquals(true, selected)
            } else if (index == 2) {
                Assert.assertEquals("Test person 3", person.name)
                Assert.assertEquals(false, selected)
            }
        }
    }

    @Test
    fun testCurrencyLiveData() {
        val currency = Currency("ISK", "Króna", 1.0)
        val currency2 = Currency("USD", "Dollar", 0.1)
        val currency3 = Currency("NOK", "Norsk krona", 0.1)

        val currencies = MutableLiveData(listOf(currency, currency2, currency3))

        val currencyCode = MutableLiveData("USD")

        val retrievedCurrencyIdsLiveData = CurrencyLiveData(currencyCode, currencies)

        val retrievedCurrency = retrievedCurrencyIdsLiveData.blockingObserve()

        Assert.assertNotNull(retrievedCurrency)

        Assert.assertEquals("USD", retrievedCurrency?.id)

        currencyCode.postValue("ISK")

        val retrievedSecondCurrency = retrievedCurrencyIdsLiveData.blockingObserve()

        Assert.assertEquals("ISK", retrievedSecondCurrency?.id)

        currencies.postValue(listOf(currency2, currency3))

        val shouldBeNullCurrency = retrievedCurrencyIdsLiveData.blockingObserve()


        Assert.assertNull(shouldBeNullCurrency)
    }

    @Test
    fun testLocalCurrencyIdsLiveData() {
        val isConnected = MutableLiveData(true)

        val currency = Currency("ISK", "Króna", 1.0)

        val currencies = MutableLiveData(listOf("ISK", "USD", "NOK"))

        val localCurrency = MutableLiveData(currency)

        val localCurrencyIdsLiveData = LocalCurrencyIdsLiveData(currencies, localCurrency, isConnected)

        val localCurrencyIds = localCurrencyIdsLiveData.blockingObserve()

        Assert.assertNotNull(localCurrencyIds)

        Assert.assertEquals(3, localCurrencyIds?.size)

        isConnected.postValue(false)

        val onlyLocalCurrency = localCurrencyIdsLiveData.blockingObserve()

        Assert.assertNotNull(onlyLocalCurrency)

        Assert.assertEquals(1, onlyLocalCurrency?.size)

        Assert.assertEquals("ISK", onlyLocalCurrency?.get(0))
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
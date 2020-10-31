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
import com.travelcy.travelcy.ui.convert.LocalAmountLiveData
import com.travelcy.travelcy.ui.split.*
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
        val totalAmount = MutableLiveData(100.0)
        val tax = MutableLiveData(20.0)
        val tip = MutableLiveData(10.0)

        val totalAmountLiveData = TotalAmountLiveData(totalAmount, tax, tip).blockingObserve()

        Assert.assertEquals(130.0, totalAmountLiveData)
    }

    @Test
    fun testTotalAmountPerPersonLiveData() {
        val localCurrency = MutableLiveData(Currency("ISK", "Króna", 1.0))
        val foreignCurrency = MutableLiveData(Currency("USD", "Dollar", 0.01))

        val sharedItem = BillItem("Pizza", 10.0, 4)

        val singleItemPerson1 = BillItem("Beer", 5.0, 2)

        val singleItemPerson2 = BillItem("Juice", 5.0, 1)

        val person1 = Person("Test person 1")
        person1.id = 1
        person1.budget = 5000.0

        val person2 = Person("Test person 2")
        person2.id = 2
        person2.budget = 2000.0

        val persons = MutableLiveData(listOf(person1, person2))

        Assert.assertEquals(2, persons.value?.size)

        val billItem1 = BillItemWithPersons(sharedItem, listOf(person1, person2))

        val billItem2 = BillItemWithPersons(singleItemPerson1, listOf(person1))

        val billItem3 = BillItemWithPersons(singleItemPerson2, listOf(person2))

        val billItemWithPersons = MutableLiveData(listOf(billItem1, billItem2, billItem3))

        val tipPercentage = MutableLiveData(0.2)

        val taxPercentage = MutableLiveData<Double?>(null)

        val totalAmountPerPersonLiveData = TotalAmountPerPersonLiveData(persons, billItemWithPersons, tipPercentage, taxPercentage, localCurrency, foreignCurrency).blockingObserve()

        Assert.assertNotNull(totalAmountPerPersonLiveData)

        totalAmountPerPersonLiveData?.forEachIndexed { index, personWithPrice ->
            val person = personWithPrice.person

            if (index == 0) {
                Assert.assertEquals("Test person 1", person.name)
                Assert.assertEquals("$36 / ISK3,600", personWithPrice.getTotalAmount())
                Assert.assertEquals(false, personWithPrice.isOverBudget())
                Assert.assertEquals("$14 / ISK1,400", personWithPrice.getRemainingFormattedBudget())
            } else if (index == 1) {
                Assert.assertEquals("Test person 2", person.name)
                Assert.assertEquals("$30 / ISK3,000", personWithPrice.getTotalAmount())
                Assert.assertEquals(true, personWithPrice.isOverBudget())
                Assert.assertEquals("($10) / (ISK1,000)", personWithPrice.getRemainingFormattedBudget())
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
    fun testLocalAmountLiveData() {
        val foreignCurrency = MutableLiveData(Currency("USD", "Dollar", 0.01))

        val foreignAmount = MutableLiveData(1.0)

        val localAmountLiveData = LocalAmountLiveData(foreignAmount, foreignCurrency)

        val localAmount1 = localAmountLiveData.blockingObserve()

        Assert.assertEquals(100.0, localAmount1)

        foreignAmount.postValue(2.0)

        val localAmount2 = localAmountLiveData.blockingObserve()

        Assert.assertEquals(200.0, localAmount2)

        foreignCurrency.postValue(Currency("CAD", "Kanadian dollar", 0.02))

        val localAmount3 = localAmountLiveData.blockingObserve()

        Assert.assertEquals(100.0, localAmount3)
    }

    @Test
    fun testAddPercentageLiveData() {
        val total = MutableLiveData(100.0)

        val percentage = MutableLiveData(0.2)

        val percentageValueLiveData = PercentageLiveData(total, percentage)

        val percentagevalue = percentageValueLiveData.blockingObserve()

        Assert.assertEquals(20.0, percentagevalue)

        percentage.postValue(0.5)

        val percentagevalue2 = percentageValueLiveData.blockingObserve()

        Assert.assertEquals(50.0, percentagevalue2)

        total.postValue(200.0)

        val percentagevalue3 = percentageValueLiveData.blockingObserve()

        Assert.assertEquals(100.0, percentagevalue3)
    }

    @Test
    fun testTipPercentageLiveData() {
        val foreignTotal = MutableLiveData(100.0)

        val billTipPercentage = MutableLiveData<Double?>(0.2)

        val billTipAmount = MutableLiveData<Double?>(null)

        val tipPercentageLiveData = TipPercentageLiveData(foreignTotal, billTipPercentage, billTipAmount)

        val tipPercentage = tipPercentageLiveData.blockingObserve()

        Assert.assertEquals(0.2, tipPercentage)

        billTipAmount.postValue(10.0)

        billTipPercentage.postValue(0.0)

        val tipPercentage2 = tipPercentageLiveData.blockingObserve()

        Assert.assertEquals(0.1, tipPercentage2)
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
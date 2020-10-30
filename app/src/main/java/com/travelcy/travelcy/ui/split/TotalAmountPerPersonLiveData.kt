package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.BillItemWithPersons
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Person
import com.travelcy.travelcy.model.PersonWithPrice

class TotalAmountPerPersonLiveData (
    private val persons: LiveData<List<Person>>,
    private val billItemsWithPersons: LiveData<List<BillItemWithPersons>>,
    private val tipPercentage: LiveData<Double>,
    private val taxPercentage: LiveData<Double?>,
    private val localCurrency: LiveData<Currency>,
    private val foreignCurrency: LiveData<Currency>
): MediatorLiveData<List<PersonWithPrice>>() {
    private fun recalculateAmountPerPerson(
        persons: List<Person>?,
        billItemsWithPersons: List<BillItemWithPersons>?,
        tipPercentage: Double,
        taxPercentage: Double,
        local: Currency?,
        foreign: Currency?
    ) {
        val pricesPerPerson = persons?.map { person ->
            val priceForPerson = billItemsWithPersons?.sumByDouble { billItemWithPersons ->
                val hasPerson =
                    billItemWithPersons.persons.find { it.id == person.id } != null
                val total =
                    billItemWithPersons.billItem.amount * billItemWithPersons.billItem.quantity * (1 + tipPercentage) * (1 + taxPercentage)

                if (hasPerson) {
                    total / billItemWithPersons.persons.size
                } else {
                    0.0
                }
            } ?: 0.0

            Pair(person, priceForPerson)
        } ?: emptyList()

        value = pricesPerPerson.filter { (_, price) ->
            price > 0
        }.map { (person, price) ->
            PersonWithPrice(person, price, local, foreign)
        }
    }

    init {
        addSource(persons) {
            recalculateAmountPerPerson(
                it,
                billItemsWithPersons.value,
                tipPercentage.value ?: 0.0,
                taxPercentage.value ?: 0.0,
                localCurrency.value,
                foreignCurrency.value
            )
        }

        addSource(billItemsWithPersons) {
            recalculateAmountPerPerson(persons.value,
                it,
                tipPercentage.value ?: 0.0,
                taxPercentage.value ?: 0.0,
                localCurrency.value,
                foreignCurrency.value
            )
        }

        addSource(tipPercentage) {
            recalculateAmountPerPerson(persons.value,
                billItemsWithPersons.value,
                it,
                taxPercentage.value ?: 0.0,
                localCurrency.value,
                foreignCurrency.value
            )
        }

        addSource(taxPercentage) {
            recalculateAmountPerPerson(persons.value,
                billItemsWithPersons.value,
                tipPercentage.value ?: 0.0,
                it ?: 0.0,
                localCurrency.value,
                foreignCurrency.value
            )
        }

        addSource(localCurrency) {
            recalculateAmountPerPerson(persons.value,
                billItemsWithPersons.value,
                tipPercentage.value ?: 0.0,
                taxPercentage.value ?: 0.0,
                it,
                foreignCurrency.value
            )
        }

        addSource(foreignCurrency) {
            recalculateAmountPerPerson(persons.value,
                billItemsWithPersons.value,
                tipPercentage.value ?: 0.0,
                taxPercentage.value ?: 0.0,
                localCurrency.value,
                it
            )
        }
    }
}
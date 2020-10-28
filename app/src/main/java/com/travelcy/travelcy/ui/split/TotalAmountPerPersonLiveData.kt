package com.travelcy.travelcy.ui.split

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.travelcy.travelcy.model.BillItemWithPersons
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.model.Person
import com.travelcy.travelcy.model.PersonWithPrice
import com.travelcy.travelcy.utils.FormatUtils

class TotalAmountPerPersonLiveData (
    private val persons: LiveData<List<Person>>,
    private val billItemsWithPersons: LiveData<List<BillItemWithPersons>>,
    private val localCurrency: LiveData<Currency>,
    private val foreignCurrency: LiveData<Currency>
): MediatorLiveData<List<PersonWithPrice>>() {
    private fun recalculateAmountPerPerson(
        persons: List<Person>?,
        billItemsWithPersons: List<BillItemWithPersons>?,
        foreign: Currency?
    ) {
        val pricesPerPerson = persons?.map { person ->
            val priceForPerson = billItemsWithPersons?.sumByDouble { billItemWithPersons ->
                val hasPerson =
                    billItemWithPersons.persons.find { it.id == person.id } != null
                val total =
                    billItemWithPersons.billItem.amount * billItemWithPersons.billItem.quantity

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
            PersonWithPrice(person, price, localCurrency.value, foreignCurrency.value)
        }
    }

    init {
        addSource(persons) {
            recalculateAmountPerPerson(it, billItemsWithPersons.value, foreignCurrency.value)
        }

        addSource(billItemsWithPersons) {
            recalculateAmountPerPerson(persons.value, it, foreignCurrency.value)
        }

        addSource(foreignCurrency) {
            recalculateAmountPerPerson(persons.value, billItemsWithPersons.value, it)
        }
    }
}
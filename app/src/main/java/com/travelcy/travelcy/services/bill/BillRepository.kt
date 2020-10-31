package com.travelcy.travelcy.services.bill

import androidx.lifecycle.LiveData
import com.travelcy.travelcy.database.dao.BillDao
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.Person
import java.util.concurrent.Executor

class BillRepository (
    private val billDao: BillDao,
    private val executor: Executor
) {
    val bill = billDao.getBill()
    val billItems = billDao.getBillItemsWithPersons()
    val persons = billDao.getAllPersons()
    var defaultPerson = billDao.getDefaultPerson()

    private fun addBillItemToBill(billItem: BillItem): BillItem {
        val id = billDao.addBillItemToBill(billItem).toInt()
        billItem.id = id
        return billItem
    }

    private fun updateBillItem(billItem: BillItem) {
        billDao.updateBillItem(billItem)
    }

    fun upsertBillItem(billItem: BillItem): BillItem {
        if(billItem.id != null) {
            updateBillItem(billItem)
            return billItem
        }

        return addBillItemToBill(billItem)
    }

    fun getBillItem(billItemId: Int): LiveData<BillItem> {
        return billDao.getBillItem(billItemId)
    }

    fun getPersonsForBillItem(billItemId: Int): LiveData<List<Person>> {
        return billDao.getPersonsForBillItem(billItemId)
    }

    fun deleteBillItem(billItem: BillItem) {
        executor.execute {
            billDao.deleteBillItem(billItem)
        }
    }

    fun addPersonToBillItem(billItem: BillItem, person: Person) {
        executor.execute {
            billDao.addPersonToBillItem(billItem, person)
        }
    }

    fun removePersonFromBillItem(billItem: BillItem, person: Person) {
        executor.execute {
            billDao.removePersonFromBillItem(billItem, person)
        }
    }

    fun updatePerson(person: Person) {
        executor.execute {
            billDao.updatePerson(person)
        }
    }

    fun addPerson(person: Person): Person {
        val id = billDao.addPerson(person)
        person.id = id.toInt()
        return person
    }

    fun upsertPerson(person: Person): Person {
        if(person.id != null) {
            updatePerson(person)
            return person
        }

        return addPerson(person)
    }

    fun saveBillItemWithPersons(billItem: BillItem, persons: List<Pair<Person, Boolean>>) {
        executor.execute {
            val upsertedBillItem = upsertBillItem(billItem)
            syncPersons(persons, upsertedBillItem)
        }
    }

    private fun syncPersons(persons: List<Pair<Person, Boolean>>, billItem: BillItem) {
        executor.execute {
            persons.forEach {(person, isSelected) ->
                val upsertedPerson = upsertPerson(person)
                if (isSelected) addPersonToBillItem(billItem, upsertedPerson)
                else removePersonFromBillItem(billItem, person)
            }
        }
    }

    fun setTipAmount(amount: Double?) {
        executor.execute {
            billDao.setTipAmount(amount)
        }
    }

    fun setTipPercentage(percentage: Double?) {
        executor.execute {
            billDao.setTipPercentage(percentage)
        }
    }

    fun setTaxPercentage(percentage: Double) {
        executor.execute {
            billDao.setTaxPercentage(percentage)
        }
    }
}
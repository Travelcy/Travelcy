package com.travelcy.travelcy.services.bill

import androidx.lifecycle.LiveData
import com.travelcy.travelcy.database.dao.BillDao
import com.travelcy.travelcy.model.Bill
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.Person
import java.util.concurrent.Executor

class BillRepository (
    private val billDao: BillDao,
    private val executor: Executor
) {
    val billItems = billDao.getBillItemsWithPersons()
    val persons = billDao.getAllPersons()

    init {
        executor.execute {
            if (!billDao.hasBill()) {
                billDao.updateBill(Bill())
            }
        }
    }

    private fun addBillItemToBill(billItem: BillItem): Int {
        return billDao.addBillItemToBill(billItem).toInt()
    }

    fun addBillItem(billItem: BillItem): Int {
        return addBillItemToBill(billItem)
    }

    fun updateBillItem(billItem: BillItem) {
        executor.execute {
            billDao.updateBillItem(billItem)
        }
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
}
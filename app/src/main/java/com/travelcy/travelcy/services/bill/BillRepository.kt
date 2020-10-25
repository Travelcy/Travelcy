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

    private fun updateBillItem(billItem: BillItem) {
        billDao.updateBillItem(billItem)
    }

    fun upsertBillItem(billItem: BillItem): Int {
        if(billItem.id != null) {
            updateBillItem(billItem)
            return billItem.id as Int
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

    fun addPersonToBillItem(billItemId: Int, person: Person) {
        executor.execute {
            billDao.addPersonToBillItem(billItemId, person)
        }
    }

    fun removePersonFromBillItem(billItemId: Int, person: Person) {
        executor.execute {
            billDao.removePersonFromBillItem(billItemId, person)
        }
    }

    fun updatePerson(person: Person) {
        executor.execute {
            billDao.updatePerson(person)
        }
    }
}
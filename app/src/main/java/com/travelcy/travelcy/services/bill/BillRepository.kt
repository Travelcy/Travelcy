package com.travelcy.travelcy.services.bill

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.travelcy.travelcy.database.dao.BillDao
import com.travelcy.travelcy.model.Bill
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.BillItemWithPersons
import java.util.concurrent.Executor

class BillRepository (
    private val billDao: BillDao,
    private val executor: Executor
) {
    private val billWithItems = billDao.getBillWithItems()

    val billItemsWithPersons = Transformations.map(billWithItems) {
        it?.items ?: emptyList()
    }

    val billPersons = Transformations.map(billWithItems) {
        it?.persons ?: emptyList()
    }

    init {
        executor.execute {
            if (!billDao.hasBill()) {
                billDao.updateBill(Bill())
            }
        }
    }

    private fun addBillItemToBill(billItem: BillItem): Int {
        if (billWithItems.value != null) {
            val id = billDao.addBillItemToBill(billItem, billWithItems.value?.bill!!)

            return id.toInt()
        }

        return -1
        // TODO error handling
    }

    fun addBillItem(billItem: BillItem): Int {
        return addBillItemToBill(billItem)
    }

    fun updateBillItem(billItem: BillItem) {
        executor.execute {
            billDao.updateBillItem(billItem)
        }
    }

    fun getBillItem(billItemId: Int): LiveData<BillItemWithPersons> {
        return billDao.getBillItemWithPersons(billItemId)
    }

    fun deleteBillItem(billItem: BillItem) {
        executor.execute {
            billDao.deleteBillItem(billItem)
        }
    }
}
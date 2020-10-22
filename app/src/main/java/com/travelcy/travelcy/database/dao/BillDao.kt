package com.travelcy.travelcy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.travelcy.travelcy.model.*


@Dao
interface BillDao {
    @Insert(onConflict = REPLACE)
    fun updateBill(bill: Bill)

    @Query("SELECT * FROM bills where id = 1")
    fun getBill(): LiveData<Bill>

    @Insert(onConflict = REPLACE)
    fun addBillItem(billItem: BillItem)

    fun addBillItemToBill(billItem: BillItem, bill: Bill) {
        billItem.billId = bill.id
        addBillItem(billItem)
    }

    @Query("INSERT INTO person_bill_item (billItemId, personId) VALUES (:billItemId, :personId)")
    fun attatchPersonToBillItem(billItemId: Int, personId: Int)

    @Insert(onConflict = REPLACE)
    fun addPerson(person: Person)

    fun addPersonToBillItem(billItem: BillItem, person: Person) {
        person.billId = billItem.billId
        addPerson(person)
        attatchPersonToBillItem(billItem.id, person.id)
    }

    @Transaction
    @Query("select * from bills where id = 1")
    fun getBillWithItems(): LiveData<BillWithItems>

    @Query("select * from bill_items")
    fun getBillItemsWithPerson() : List<BillItemWithPersons>
}
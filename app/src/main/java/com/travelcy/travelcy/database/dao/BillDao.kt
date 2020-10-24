package com.travelcy.travelcy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.travelcy.travelcy.model.*


@Dao
interface BillDao {
    @Insert(onConflict = REPLACE)
    fun updateBill(bill: Bill)

    @Query("SELECT COUNT(*) FROM bills")
    fun hasBill(): Boolean

    @Query("SELECT * FROM bills where id = 1")
    fun getBill(): Bill

    @Insert(onConflict = REPLACE)
    fun addBillItem(billItem: BillItem): Long

    @Update
    fun updateBillItem(billItem: BillItem)

    fun addBillItemToBill(billItem: BillItem, bill: Bill): Long {
        billItem.billId = bill.id
        return addBillItem(billItem)
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

    @Query("select * from bill_items where id = :billItemId")
    fun getBillItemWithPersons(billItemId: Int): LiveData<BillItemWithPersons>

    @Delete
    fun deleteBillItem(billItem: BillItem)
}
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
    fun getBill(): LiveData<Bill>

    @Insert(onConflict = REPLACE)
    fun addBillItem(billItem: BillItem): Long

    @Update
    fun updateBillItem(billItem: BillItem)

    fun addBillItemToBill(billItem: BillItem): Long {
        billItem.billId = 1
        return addBillItem(billItem)
    }

    @Query("DELETE FROM person_bill_item where billItemId = :billItemId and personId = :personId")
    fun detatchPersonFromBillItem(billItemId: Int, personId: Int)

    @Query("INSERT INTO person_bill_item (billItemId, personId) VALUES (:billItemId, :personId)")
    fun attatchPersonToBillItem(billItemId: Int, personId: Int)

    @Insert(onConflict = REPLACE)
    fun addPerson(person: Person): Long

    fun addPersonToBillItem(billItem: BillItem, person: Person) {
        person.billId = billItem.billId
        val personId = addPerson(person)
        attatchPersonToBillItem(billItem.id, personId.toInt())
    }

    fun removePersonFromBillItem(billItem: BillItem, person: Person) {
        detatchPersonFromBillItem(billItem.id, person.id)
    }

    @Query("select * from bill_items where billId = 1")
    fun getBillItemsWithPersons(): LiveData<List<BillItemWithPersons>>

    @Query("select * from persons p left join person_bill_item pi on pi.personId = p.id where pi.billItemId = :billItemId")
    fun getPersonsForBillItem(billItemId: Int): LiveData<List<Person>>

    @Update
    fun updatePerson(person: Person)

    @Query("select * from persons")
    fun getAllPersons(): LiveData<List<Person>>

    @Query("select * from bill_items where id = :billItemId")
    fun getBillItem(billItemId: Int): LiveData<BillItem>

    @Delete
    fun deleteBillItem(billItem: BillItem)
}
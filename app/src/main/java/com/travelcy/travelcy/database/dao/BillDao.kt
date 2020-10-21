package com.travelcy.travelcy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.travelcy.travelcy.model.Bill
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.BillWithItems


@Dao
interface BillDao {
    @Insert(onConflict = REPLACE)
    fun updateBill(bill: Bill)

    @Query("SELECT * FROM bills where id = 1")
    fun getBill(): LiveData<Bill>

    @Insert(onConflict = REPLACE)
    fun addBillItem(billItem: BillItem)

    @Transaction
    @Query("select * from bills where id = 1")
    fun getBillWithItems(): LiveData<BillWithItems>
}